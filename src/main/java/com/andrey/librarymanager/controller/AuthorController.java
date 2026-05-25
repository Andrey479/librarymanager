package com.andrey.librarymanager.controller;

import com.andrey.librarymanager.dto.AuthorRequestDTO;
import com.andrey.librarymanager.dto.AuthorResponseDTO;
import com.andrey.librarymanager.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<AuthorResponseDTO> createAuthor(@RequestBody AuthorRequestDTO authorRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.register(authorRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<AuthorResponseDTO>> listAll(@PageableDefault() Pageable pageable){
        Page<AuthorResponseDTO> response = authorService.listAll(pageable);
        return ResponseEntity.ok(response);
    }
}
