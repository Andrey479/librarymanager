package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.DashboardResponseDTO;
import com.andrey.librarymanager.model.LoanStatus;
import com.andrey.librarymanager.repository.BookRepository;
import com.andrey.librarymanager.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public Long countNumberOfBooks(){
        log.info("Total book count completed successfully");
        return bookRepository.count();
    }

    public Long countBorrowedBooks(){
        log.info("Total count of books borrowed successfully");
        return loanRepository.countByStatus(LoanStatus.ACTIVE);
    }

    public Long countOverdueLoans(){
        log.info("Counting late loans successfully");
        return loanRepository.countByStatus(LoanStatus.OVERDUE);
    }

    public BigDecimal sumAllFines(){
        log.info("All fines were calculated successfully");
        return loanRepository.sumAllFines();
    }

    public DashboardResponseDTO getDashboard(){
        log.info("The dashboard loaded correctly");
        return new DashboardResponseDTO(
                countNumberOfBooks(),
                countBorrowedBooks(),
                countOverdueLoans(),
                sumAllFines()
        );
    }
}
