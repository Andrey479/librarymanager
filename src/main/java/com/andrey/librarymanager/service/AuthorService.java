package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.AuthorRequestDTO;
import com.andrey.librarymanager.dto.AuthorResponseDTO;
import com.andrey.librarymanager.model.Author;
import com.andrey.librarymanager.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorResponseDTO register(AuthorRequestDTO request) {
        log.info("Author successfully registered.");
        return toResponse(authorRepository.save(toEntity(request)));
    }

    public Page<AuthorResponseDTO> listAll(Pageable pageable) {
        Page<Author> authors = authorRepository.findAll(pageable);
        log.info("Success in listing all authors.");

        return authors.map(this::toResponse);
    }

    private Author toEntity(AuthorRequestDTO request) {
        Author author = new Author();
        author.setName(request.getName());
        author.setNationality(request.getNationality());
        author.setBirthDate(request.getBirthDate());
        return author;
    }

    private AuthorResponseDTO toResponse(Author author) {
        AuthorResponseDTO authorResponseDTO = new AuthorResponseDTO();
        authorResponseDTO.setId(author.getId());
        authorResponseDTO.setName(author.getName());
        authorResponseDTO.setBirthDate(author.getBirthDate());
        authorResponseDTO.setNationality(author.getNationality());
        return authorResponseDTO;
    }
}