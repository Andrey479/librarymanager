package com.andrey.librarymanager.repository;

import com.andrey.librarymanager.model.Loan;
import com.andrey.librarymanager.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookIdAndUserIdAndStatus(Long bookId, Long userId, LoanStatus status);
    List<Loan> findAllByStatus(LoanStatus loanStatus);
}