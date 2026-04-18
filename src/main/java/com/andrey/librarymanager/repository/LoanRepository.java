package com.andrey.librarymanager.repository;

import com.andrey.librarymanager.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
}