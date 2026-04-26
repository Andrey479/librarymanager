package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.AuthorRequestDTO;
import com.andrey.librarymanager.dto.AuthorResponseDTO;
import com.andrey.librarymanager.model.Author;
import com.andrey.librarymanager.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorResponseDTO register(AuthorRequestDTO request) {
        return toResponse(authorRepository.save(toEntity(request)));
    }

    public List<AuthorResponseDTO> listAll() {
        return authorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
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