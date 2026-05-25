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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldListAllAuthors () {
        //given
        List<Author> authors = new ArrayList<>();
        authors.add(Author.builder().id(7L).build());

        Pageable pageable = PageRequest.of(0, 10);
        Page<Author> authorPage = new PageImpl<>(
                authors,
                pageable,
                authors.size()
        );

        //when
        when(authorRepository.findAll(pageable)).thenReturn(authorPage);
        Page<AuthorResponseDTO> result = authorService.listAll(pageable);

        //assert
        assertEquals(7L, result.getContent().getFirst().getId());
    }
}
