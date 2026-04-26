package com.andrey.librarymanager;

import com.andrey.librarymanager.dto.AuthorRequestDTO;
import com.andrey.librarymanager.dto.AuthorResponseDTO;
import com.andrey.librarymanager.model.Author;
import com.andrey.librarymanager.repository.AuthorRepository;
import com.andrey.librarymanager.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void registerTest () {
        //given
        AuthorRequestDTO authorRequestDTO = new AuthorRequestDTO();
        Author author = new Author();

        //when
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        AuthorResponseDTO authorSaved = authorService.register(authorRequestDTO);

        //then
        assertNotNull(authorSaved);
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void listAllTest () {
        //given
        List<AuthorResponseDTO> authorResponseList;
        List<Author> authors = List.of();

        //when
        when(authorRepository.findAll()).thenReturn(authors);
        authorResponseList = authorService.listAll();

        //then
        assertNotNull(authorResponseList);
        verify(authorRepository).findAll();
    }
}
