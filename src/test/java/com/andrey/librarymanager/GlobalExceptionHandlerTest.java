package com.andrey.librarymanager;

import com.andrey.librarymanager.dto.LoanRequestDTO;
import com.andrey.librarymanager.exception.BusinessException;
import com.andrey.librarymanager.exception.ResourceNotFoundException;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsServiceImpl;
import com.andrey.librarymanager.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private LoanService loanService;
    @MockitoBean private AuthorService authorService;
    @MockitoBean private BookService bookService;
    @MockitoBean private UserService userService;
    @MockitoBean private AuthService authService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;
    @MockitoBean private DashboardService dashboardService;

    @Test
    void shouldResourceNotFoundException() throws Exception{
        when(loanService.register(any(LoanRequestDTO.class))).thenThrow(new ResourceNotFoundException(""));
        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new LoanRequestDTO(1L, 1L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBusinessException() throws Exception {

        when(loanService.register(any(LoanRequestDTO.class))).thenThrow(new BusinessException(""));

        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new LoanRequestDTO(1L, 1L))))
                .andExpect(status().isUnprocessableContent());
    }
}
