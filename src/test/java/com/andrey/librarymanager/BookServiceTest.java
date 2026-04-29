package com.andrey.librarymanager;

import com.andrey.librarymanager.dto.BookRequestDTO;
import com.andrey.librarymanager.dto.BookResponseDTO;
import com.andrey.librarymanager.model.Author;
import com.andrey.librarymanager.model.Book;
import com.andrey.librarymanager.repository.AuthorRepository;
import com.andrey.librarymanager.repository.BookRepository;
import com.andrey.librarymanager.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldRegisterBookSuccessfully() {
        //given
        Author author = new Author();
        author.setId(1L);

        BookRequestDTO bookRequestDTO = new BookRequestDTO();
        bookRequestDTO.setTitle("title");
        bookRequestDTO.setIsbn("isbn");
        bookRequestDTO.setPublicationYear(1997);
        bookRequestDTO.setTotalCopies(100);
        bookRequestDTO.setAuthorsId(Set.of(1L));

        Book book = new Book(
                1L,
                "title",
                "isbn",
                1997,
                100,
                100,
                Set.of(author)
        );

        //when
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(authorRepository.findAllById(Set.of(1L))).thenReturn(List.of(author));
        BookResponseDTO bookResponseDTO = bookService.register(bookRequestDTO);

        //then
        assertNotNull(bookResponseDTO);
        assertEquals(bookResponseDTO.getAvailableCopies(), bookResponseDTO.getTotalCopies());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenNoAuthorsProvided() {
        //given
        BookRequestDTO bookRequestDTO = new BookRequestDTO();
        bookRequestDTO.setTitle("title");
        bookRequestDTO.setIsbn("isbn");
        bookRequestDTO.setTotalCopies(100);
        bookRequestDTO.setPublicationYear(1990);
        bookRequestDTO.setAuthorsId(Set.of());

        //then
        assertThrows(RuntimeException.class, () -> bookService.register(bookRequestDTO));
    }
}
