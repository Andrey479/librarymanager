package com.andrey.librarymanager.controller;

import com.andrey.librarymanager.dto.BookRequestDTO;
import com.andrey.librarymanager.dto.BookResponseDTO;
import com.andrey.librarymanager.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponseDTO> create (@Valid @RequestBody BookRequestDTO bookRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.register(bookRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDTO>> listAll(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.listAll());
    }
}
