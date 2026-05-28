package com.andrey.librarymanager;

import com.andrey.librarymanager.dto.BookLoanCountProjection;
import com.andrey.librarymanager.dto.LoanRequestDTO;
import com.andrey.librarymanager.dto.LoanResponseDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.model.Book;
import com.andrey.librarymanager.model.Loan;
import com.andrey.librarymanager.model.LoanStatus;
import com.andrey.librarymanager.model.User;
import com.andrey.librarymanager.repository.BookRepository;
import com.andrey.librarymanager.repository.LoanRepository;
import com.andrey.librarymanager.repository.UserRepository;
import com.andrey.librarymanager.service.LoanService;
import com.andrey.librarymanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock private LoanRepository loanRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserService userService;

    @InjectMocks
    private LoanService loanService;

    @Test
    void shouldCreateLoanSuccessfullyWhenBookIsAvailable(){
        //arrange
        LoanRequestDTO loanRequestDTO = new LoanRequestDTO(1L, 1L);
        Book book = Book.builder()
                .id(1L)
                .availableCopies(100)
                .build();

        Optional<Book> optionalBook = Optional.of(book);
        Optional<User> optionalUser = Optional.of(new User());

        Loan loan = Loan.builder()
                    .book(optionalBook.get())
                    .user(optionalUser.get())
                    .loanDate(LocalDate.now())
                    .expectedReturnDate(LocalDate.now().plusDays(14L))
                    .status(LoanStatus.ACTIVE)
                    .fine(BigDecimal.ZERO)
                    .build();

        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(bookRepository.findById(any(Long.class))).thenReturn(optionalBook);
        when(userRepository.findById(any(Long.class))).thenReturn(optionalUser);

        //verify
        assertNotNull(loanService.register(loanRequestDTO));
        verify(loanRepository).save(any(Loan.class));
        verify(bookRepository).findById(any(Long.class));
        verify(userRepository).findById(any(Long.class));
    }

    @Test
    void shouldThrowExceptionWhenBookIsNotAvailable(){

        //act + assert
        assertThrows(RuntimeException.class, () -> loanService.register(new LoanRequestDTO(1L, 1L)));
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyHasActiveLoanForSameBook() {
        //assert
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .phone("99999999")
                .build();

        Book book = Book.builder()
                .id(1L)
                .title("title")
                .isbn("ISBN")
                .publicationYear(2000)
                .totalCopies(1000)
                .availableCopies(1)
                .authors(Set.of())
                .build();

        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(loanRepository.existsByBookIdAndUserIdAndStatus(1L, 1L, LoanStatus.ACTIVE)).thenReturn(true);

        //act + assert
        assertThrows(RuntimeException.class, () -> loanService.register(new LoanRequestDTO(1L, 1L)));
    }

    @Test
    void shouldDecrementAvailableCopiesWhenLoanIsCreated(){
        //arrange
        Book book = Book.builder()
                .id(1L)
                .availableCopies(10)
                .build();

        User user = User.builder()
                .id(1L)
                .build();

        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusDays(14L))
                .status(LoanStatus.ACTIVE)
                .fine(BigDecimal.ZERO)
                .build();

        when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book));
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(loanRepository.existsByBookIdAndUserIdAndStatus(1L, 1L, LoanStatus.ACTIVE)).thenReturn(false);

        //act
        loanService.register(new LoanRequestDTO(1L, 1L));
        //arrange
        assertEquals(9, book.getAvailableCopies());
    }

    @Test
    void shouldCalculateFineCorrectlyWhenReturnIsLate() {
        //arrange
        Book book = Book.builder()
                .id(1L)
                .availableCopies(10)
                .build();

        User user = User.builder()
                .id(1L)
                .build();

        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusDays(14L))
                .fine(BigDecimal.ZERO)
                .status(LoanStatus.ACTIVE)
                .build();

        when(loanRepository.findById(any(Long.class))).thenReturn(Optional.of(loan));

        //act
        loanService.returnLoan(loan.getId(), LocalDate.now().plusDays(24L));

        //assert
        assertEquals(new BigDecimal("20"), loan.getFine());
    }

    @Test
    void shouldNotApplyFineWhenReturnIsOnTime(){
        //arrange
        Book book = Book.builder()
                .id(1L)
                .availableCopies(10)
                .build();

        User user = User.builder()
                .id(1L)
                .build();

        Loan loan = Loan.builder()
                .id(1L)
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .expectedReturnDate(LocalDate.now().plusDays(14L))
                .fine(BigDecimal.ZERO)
                .status(LoanStatus.ACTIVE)
                .build();

        when(loanRepository.findById(any(Long.class))).thenReturn(Optional.of(loan));

        //act
        loanService.returnLoan(loan.getId(), LocalDate.now().plusDays(5L));

        //assert
        assertEquals(BigDecimal.ZERO, loan.getFine());
    }

    @Test
    void shouldListLoansByStatus(){


        Loan loan1 = Loan.builder()
                .book(Book.builder().build())
                .user(User.builder().build())
                .status(LoanStatus.ACTIVE)
                .build();

        Loan loan3 = Loan.builder()
                .book(Book.builder().build())
                .user(User.builder().build())
                .status(LoanStatus.ACTIVE)
                .build();

        List<Loan> loanList = new ArrayList<>();
        loanList.add(loan1);
        loanList.add(loan3);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Loan> mockPage = new PageImpl<>(loanList, pageable, loanList.size());

        when(loanRepository.findAllByStatus(any(LoanStatus.class), any(Pageable.class))).thenReturn(mockPage);

        //act
        Page<LoanResponseDTO> loansByStatus = loanService.listLoansByStatus(LoanStatus.ACTIVE, pageable);

        //asert
        assertNotNull(loansByStatus);
        assertEquals(2, loansByStatus.getContent().size());
    }

    @Test
    void shouldMostBorrowedBooks(){

        //tem que ser assim porquê não faz sentido fazer lista de loans
        BookLoanCountProjection proj1 = mock(BookLoanCountProjection.class);
        when(proj1.getQuantity()).thenReturn(10L);

        BookLoanCountProjection proj2 = mock(BookLoanCountProjection.class);
        when(proj2.getQuantity()).thenReturn(5L);

        List<BookLoanCountProjection> projectionList = new ArrayList<>();
        projectionList.add(proj1);
        projectionList.add(proj2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<BookLoanCountProjection> loanPage = new PageImpl<>(
                projectionList,
                pageable,
                projectionList.size()
        );

        when(loanRepository.findMostBorrowedBooks(any(Pageable.class))).thenReturn(loanPage);

        //act
        Page<BookLoanCountProjection> countProjections = loanService.listMostBorrowedBooks(pageable);

        //assert
        assertEquals(10L, countProjections.getContent().getFirst().getQuantity());
        assertEquals(5L, countProjections.getContent().getLast().getQuantity());
    }

    @Test
    void shouldListLoansIfOverdue(){
        User andrey = User.builder().id(1L).name("Andrey").build();
        User claude = User.builder().id(2L).name("Claude").build();

        List<User> userList = new ArrayList<>();
        userList.add(andrey);
        userList.add(claude);

        Pageable pageable = PageRequest.of(0, 10);

        Page<User> userPage = new PageImpl<>(
                userList,
                pageable,
                userList.size()
        );

        when(loanRepository.findUsersByStatus(LoanStatus.OVERDUE, pageable)).thenReturn(userPage);
        when(userService.toResponse(andrey)).thenReturn(new UserResponseDTO(1L, "Andrey", null, null));
        when(userService.toResponse(claude)).thenReturn(new UserResponseDTO(2L, "Claude", null, null));

        //act
        Page<UserResponseDTO> users = loanService.listUsersWithOverdueLoans(LoanStatus.OVERDUE, pageable);

        //assert
        assertEquals("Andrey", users.getContent().getFirst().getName());
        assertEquals("Claude", users.getContent().getLast().getName());
        assertEquals(2, users.getContent().size());
    }
}
