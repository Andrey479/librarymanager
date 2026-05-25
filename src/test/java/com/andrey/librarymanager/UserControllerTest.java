package com.andrey.librarymanager;

import com.andrey.librarymanager.controller.UserController;
import com.andrey.librarymanager.dto.UserRequestDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsServiceImpl;
import com.andrey.librarymanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldRegisterUserSuccessfully () throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "user",
                "email@email.com",
                "5555555555",
                "senha secreta"
        );

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenUserRequestIsEmpty () throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new UserRequestDTO())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListAllUsers() throws Exception{
        List<UserResponseDTO> emptyList = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponseDTO> mockPage = new PageImpl<>(emptyList, pageable, 0);

        when(userService.listAll(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }
}
