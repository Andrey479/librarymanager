package com.andrey.librarymanager.repository;

import com.andrey.librarymanager.dto.BookLoanCountProjection;
import com.andrey.librarymanager.model.Loan;
import com.andrey.librarymanager.model.LoanStatus;
import com.andrey.librarymanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByBookIdAndUserIdAndStatus(Long bookId, Long userId, LoanStatus status);
    Page<Loan> findAllByStatus(LoanStatus loanStatus, Pageable pageable);
    @Query("SELECT l.book.id AS bookId, COUNT(l) AS quantidade FROM Loan l GROUP BY l.book.id ORDER BY COUNT(l) DESC")
    Page<BookLoanCountProjection> findMostBorrowedBooks(Pageable pageable);
    @Query("SELECT DISTINCT l.user FROM Loan l WHERE l.status = :status")
    Page<User> findUsersByStatus(@Param("status") LoanStatus status, Pageable pageable);
}