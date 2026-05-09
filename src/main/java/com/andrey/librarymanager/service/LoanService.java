package com.andrey.librarymanager.service;

import com.andrey.librarymanager.dto.LoanRequestDTO;
import com.andrey.librarymanager.dto.LoanResponseDTO;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public LoanResponseDTO register(LoanRequestDTO request){
        Optional<Book> optionalBook = bookRepository.findById(request.getBookId());
        Optional<User> optionalUser = userRepository.findById(request.getUserId());

        if (optionalBook.isEmpty() || optionalUser.isEmpty()){
            throw new ResourceNotFoundException("Book or User without content");
        }

        Book book = optionalBook.get();
        if (book.getAvailableCopies() == 0){
            throw new BusinessException("No copies of this book are available for loan.");
        }

        boolean hasActiveLoan = loanRepository.existsByBookIdAndUserIdAndStatus(
                request.getBookId(),
                request.getUserId(),
                LoanStatus.ACTIVE
        );
        if (hasActiveLoan){
            throw new BusinessException("This book already has an active loan with the user.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return toResponse(loanRepository.save(toEntity(book, optionalUser.get())));
    }

    public LoanResponseDTO returnLoan(Long loanId, LocalDate returnDate){

        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (optionalLoan.isEmpty()){
            throw new ResourceNotFoundException("Loan without content");
        }

        Loan loan = optionalLoan.get();
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

        return toResponse(loan);
    }

    public List<LoanResponseDTO> listLoansByStatus(LoanStatus loanStatus){
        return loanRepository.findAllByStatus(loanStatus)
                .stream()
                .map(this::toResponse)
                .toList();
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
}
