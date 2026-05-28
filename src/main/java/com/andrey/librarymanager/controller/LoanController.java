package com.andrey.librarymanager.controller;

import com.andrey.librarymanager.dto.BookLoanCountProjection;
import com.andrey.librarymanager.dto.LoanRequestDTO;
import com.andrey.librarymanager.dto.LoanResponseDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.model.LoanStatus;
import com.andrey.librarymanager.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponseDTO> createLoan(@Valid @RequestBody LoanRequestDTO loanRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.register(loanRequestDTO));
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponseDTO> returnLoan(
            @Valid
            @PathVariable Long id,
            @RequestParam LocalDate returnDate){
        return ResponseEntity.status(HttpStatus.OK).body(loanService.returnLoan(id, returnDate));
    }

    @GetMapping
    public ResponseEntity<Page<LoanResponseDTO>> returnLoanByStatus (
        @Valid
        @RequestParam LoanStatus loanStatus,
        @PageableDefault Pageable pageable
    ) {
        Page<LoanResponseDTO> response = loanService.listLoansByStatus(loanStatus, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue-users")
    public ResponseEntity<Page<UserResponseDTO>> listUsersWithOverdueLoans(
            @PageableDefault Pageable pageable,
            @RequestParam LoanStatus loanStatus
    ){
        Page<UserResponseDTO> response = loanService.listUsersWithOverdueLoans(loanStatus, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/most-borrowed")
    public ResponseEntity<Page<BookLoanCountProjection>> listMostBorrowedBooks(
            @PageableDefault Pageable pageable
    ){
        Page<BookLoanCountProjection> response = loanService.listMostBorrowedBooks(pageable);
        return ResponseEntity.ok(response);
    }
}
