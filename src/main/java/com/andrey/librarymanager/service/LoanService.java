package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.BookLoanCountProjection;
import com.andrey.librarymanager.dto.LoanRequestDTO;
import com.andrey.librarymanager.dto.LoanResponseDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.exception.BusinessException;
import com.andrey.librarymanager.exception.ResourceNotFoundException;
import com.andrey.librarymanager.model.Book;
import com.andrey.librarymanager.model.Loan;
import com.andrey.librarymanager.model.LoanStatus;
import com.andrey.librarymanager.model.User;
import com.andrey.librarymanager.repository.BookRepository;
import com.andrey.librarymanager.repository.LoanRepository;
import com.andrey.librarymanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public LoanResponseDTO register(LoanRequestDTO request){
        User user = validateOptionalUser(request);
        Book book = validateOptionalBook(request);

        validateIfHasActiveLoan(request);

        log.info("Loan successfully registered.");
        return toResponse(loanRepository.save(toEntity(book, user)));
    }

    public LoanResponseDTO returnLoan(Long loanId, LocalDate returnDate){

        Loan loan = validateLoan(loanId);
        int result = loan.getExpectedReturnDate().compareTo(returnDate);

        if (result < 0){

            BigDecimal fine = new BigDecimal("2");
            long days = ChronoUnit.DAYS.between(loan.getExpectedReturnDate(), returnDate);

            BigDecimal fineValue = fine.multiply(new BigDecimal(days));
            loan.setFine(fineValue);
        }

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        loan.setStatus(LoanStatus.RETURNED);
        loan.setBook(book);

        log.info("A loan was repaid.");
        return toResponse(loan);
    }

    public Page<LoanResponseDTO> listLoansByStatus(LoanStatus loanStatus, Pageable pageable){
        Page<Loan> loans = loanRepository.findAllByStatus(loanStatus, pageable);
        log.info("All the loans were listed.");
        return loans.map(this::toResponse);
    }

    public Page<BookLoanCountProjection> listMostBorrowedBooks(Pageable pageable){
        Page<BookLoanCountProjection> countProjections = loanRepository.findMostBorrowedBooks(pageable);
        log.info("List of the most borrowed books");
        return countProjections;
    }

    public Page<UserResponseDTO> listUsersWithOverdueLoans(LoanStatus loanStatus, Pageable pageable){
        Page<User> users = loanRepository.findUsersByStatus(loanStatus, pageable);
        log.info("Listed users with status: {}", loanStatus.toString());
        return users.map(userService::toResponse);
    }

    private LoanResponseDTO toResponse(Loan loan){
        return new LoanResponseDTO(
                loan.getId(),
                loan.getBook().getId(),
                loan.getUser().getId(),
                loan.getLoanDate(),
                loan.getExpectedReturnDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                loan.getFine()
        );
    }

    private Loan toEntity(Book book, User user){
        return Loan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusDays(14L))
                .status(LoanStatus.ACTIVE)
                .fine(BigDecimal.ZERO)
                .build();
    }

    private Book validateOptionalBook(LoanRequestDTO request) {

        Optional<Book> optionalBook = bookRepository.findById(request.getBookId());

        if (optionalBook.isEmpty()){
            log.warn("Book is empty");
            throw new ResourceNotFoundException("Book without content");
        }

        Book book = optionalBook.get();
        validateAvailableCopies(book);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return book;
    }

    private User validateOptionalUser(LoanRequestDTO request){
        Optional<User> optionalUser = userRepository.findById(request.getUserId());

        if (optionalUser.isEmpty()) {
            log.warn("User is empty");
            throw new ResourceNotFoundException("User without concent");
        }
        return optionalUser.get();
    }

    private void validateAvailableCopies(Book book){
        if (book.getAvailableCopies() == 0){
            log.warn("No copies of this book are available for loan. {}", 0);
            throw new BusinessException("No copies of this book are available for loan.");
        }
    }

    private void validateIfHasActiveLoan(LoanRequestDTO request){
        boolean hasActiveLoan = loanRepository.existsByBookIdAndUserIdAndStatus(
                request.getBookId(),
                request.getUserId(),
                LoanStatus.ACTIVE
        );
        if (hasActiveLoan){
            log.warn("This book already has an active loan with the user.");
            throw new BusinessException("This book already has an active loan with the user.");
        }
    }

    private Loan validateLoan(Long loanId){
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (optionalLoan.isEmpty()){
            log.warn("Loan without content");
            throw new ResourceNotFoundException("Loan without content");
        }
        return optionalLoan.get();
    }
}
