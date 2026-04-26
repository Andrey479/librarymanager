package com.andrey.librarymanager.repository;

import com.andrey.librarymanager.model.Loan;
import com.andrey.librarymanager.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookIdAndStatus(Long id, LoanStatus status);
}