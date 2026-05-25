package com.andrey.librarymanager.repository;

import com.andrey.librarymanager.model.Loan;
import com.andrey.librarymanager.model.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookIdAndUserIdAndStatus(Long bookId, Long userId, LoanStatus status);
    Page<Loan> findAllByStatus(LoanStatus loanStatus, Pageable pageable);
}