package com.andrey.librarymanager.controller;

import com.andrey.librarymanager.dto.AuthorRequestDTO;
import com.andrey.librarymanager.dto.AuthorResponseDTO;
import com.andrey.librarymanager.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<AuthorResponseDTO>> listAll(){
        return ResponseEntity.status(HttpStatus.OK).body(authorService.listAll());
    }
}
