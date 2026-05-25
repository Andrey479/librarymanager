package com.andrey.librarymanager.controller;

import com.andrey.librarymanager.dto.BookRequestDTO;
import com.andrey.librarymanager.dto.BookResponseDTO;
import com.andrey.librarymanager.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<BookResponseDTO>> listAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Boolean available,
            @PageableDefault() Pageable pageable
    ){
        Page<BookResponseDTO> response =bookService.listAllByFilter(title, authorId, available, pageable);
        return ResponseEntity.ok(response);
    }
}
