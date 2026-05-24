package com.andrey.librarymanager;

import com.andrey.librarymanager.controller.BookController;
import com.andrey.librarymanager.dto.BookRequestDTO;
import com.andrey.librarymanager.dto.BookResponseDTO;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsServiceImpl;
import com.andrey.librarymanager.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldRegisterBookSuccessfully() throws Exception {
        BookRequestDTO bookRequestDTO = new BookRequestDTO(
                "title",
                "isbn",
                2000,
                100,
                Set.of(1L)
        );

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldBeAnErrorWhenRegisteringTheBook() throws Exception {
        BookRequestDTO bookRequestDTO = new BookRequestDTO(
                "title",
                "isbn",
                2000,
                100,
                Set.of()
        );

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(bookRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListAllBooks() throws Exception{
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shoulListBooksByTitle() throws Exception{
        BookResponseDTO bookResponseDTO = new BookResponseDTO(
                1L,
                "Código limpo",
                "978-8576082675",
                2009,
                100,
                100
        );

        List<BookResponseDTO> bookResponse;
        bookResponse = new ArrayList<>();
        bookResponse.add(bookResponseDTO);

        when(bookService.listAllByFilter("Código", null, null))
                .thenReturn(bookResponse);

        mockMvc.perform(get("/api/books?title=Código"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].title").value("Código limpo"));
    }
}
