package com.andrey.librarymanager;

import com.andrey.librarymanager.dto.BookResponseDTO;
import com.andrey.librarymanager.model.Author;
import com.andrey.librarymanager.model.Book;
import com.andrey.librarymanager.repository.BookRepository;
import com.andrey.librarymanager.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceFiltersTest {

    @Mock private BookRepository bookRepository;
    @InjectMocks private BookService bookService;

    Pageable pageable;

    //todos os testes vão retornar a mesma entidade
    //porque o objetivo é testar os filtros o retorno será testado no controller
    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp(){
        Author author = Author.builder()
                .id(1L)
                .name("Robert C. Martin")
                .nationality("Americano")
                .birthDate(LocalDate.of(1952, 12, 5))
                .build();

        Book book = Book.builder()
                .id(1L)
                .title("Código limpo: habilidades práticas do Agile Software")
                .isbn("978-8576082675")
                .publicationYear(2009)
                .totalCopies(100)
                .availableCopies(100)
                .authors(Set.of(author))
                .build();

        List<Book> bookList = new ArrayList<>();
        bookList.add(book);
        pageable = PageRequest.of(0, 10);
        Page<Book> mockPage = new PageImpl<>(bookList, pageable, bookList.size());

        when(bookRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(mockPage);
    }

    @Test
    void shouldReturnBookByTitle(){
        Page<BookResponseDTO> result = bookService.listAllByFilter("Clean", null, null, pageable);
        assertEquals("Código limpo: habilidades práticas do Agile Software", result.getContent().getFirst().getTitle());
    }

    @Test
    void shouldReturnBookByAuthor(){
        Page<BookResponseDTO> result = bookService.listAllByFilter(null, 1L, null, pageable);
        assertEquals(1L, result.getContent().getFirst().getId());
    }

    @Test
    void shouldReturnBookByAvailability(){
        Page<BookResponseDTO> result = bookService.listAllByFilter(null, null, Boolean.TRUE, pageable);
        assertEquals("978-8576082675", result.getContent().getFirst().getIsbn());
    }

    @Test
    void shouldReturnBookByTitleAndAuthor() {
        Page<BookResponseDTO> result = bookService.listAllByFilter("Código limpo: habilidades práticas do Agile Software",
                1L, null, pageable);
        assertEquals(2009, result.getContent().getFirst().getPublicationYear());
    }

    @Test
    void shouldReturnBookByTitleAndAvailability(){
        Page<BookResponseDTO> result = bookService.listAllByFilter("Clean", null, Boolean.TRUE, pageable);
        assertEquals(100, result.getContent().getFirst().getTotalCopies());
    }

    @Test
    void shouldReturnBookByAuthorAndAvailability(){
        Page<BookResponseDTO> result = bookService.listAllByFilter(null, 1L, Boolean.TRUE, pageable);
        assertEquals(100, result.getContent().getFirst().getAvailableCopies());
    }

    @Test
    void shouldReturnBookByTitleAndAuthorAndAvailability(){
        Page<BookResponseDTO> result = bookService.listAllByFilter("Codigo Limpo", 1L, Boolean.TRUE, pageable);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}