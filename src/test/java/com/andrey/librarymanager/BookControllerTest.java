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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
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
        Page<BookResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(bookService.listAllByFilter(any(), any(), any(), any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
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

        PageRequest pageRequest = PageRequest.of(0,10);

        Page<BookResponseDTO> pageBook = new PageImpl<>(
                bookResponse,
                pageRequest,
                bookResponse.size()
        );

        when(bookService.listAllByFilter(any(), any(), any(), any(Pageable.class))).thenReturn(pageBook);

        mockMvc.perform(get("/api/books")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Código limpo"));
    }
}
