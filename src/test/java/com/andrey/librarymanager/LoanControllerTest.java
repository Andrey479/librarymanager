package com.andrey.librarymanager;

import com.andrey.librarymanager.controller.LoanController;
import com.andrey.librarymanager.dto.BookLoanCountProjection;
import com.andrey.librarymanager.dto.LoanRequestDTO;
import com.andrey.librarymanager.dto.LoanResponseDTO;
import com.andrey.librarymanager.dto.UserResponseDTO;
import com.andrey.librarymanager.model.LoanStatus;
import com.andrey.librarymanager.security.JwtService;
import com.andrey.librarymanager.security.UserDetailsServiceImpl;
import com.andrey.librarymanager.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanService loanService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldRegisterLoanSuccessfully() throws Exception {
        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new LoanRequestDTO(1L, 1L))))
                .andExpect(status().isCreated());
    }

    @Test
    void shoudNotRegisterLoanWhenFieldsAreNull() throws Exception {
        mockMvc.perform(post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(new LoanRequestDTO(null, null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnLoanSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/loans/1/return")
                        .param("returnDate", LocalDate.now().toString()))
                        .andExpect(status().isOk());
    }

    @Test
    void shouldReturnLoanByStatus() throws Exception {

        List<LoanResponseDTO> loanList = new ArrayList<>();
        LoanResponseDTO loan1 = new LoanResponseDTO();
        LoanResponseDTO loan2 = new LoanResponseDTO();

        loan1.setBookId(1L);
        loan1.setUserId(1L);
        loan1.setId(3L);
        loan1.setStatus(LoanStatus.ACTIVE);

        loan2.setBookId(5L);
        loan2.setUserId(5L);
        loan2.setId(19L);
        loan2.setStatus(LoanStatus.ACTIVE);

        loanList.add(loan1);
        loanList.add(loan2);

        PageRequest pageRequest = PageRequest.of(0,10);
        Page<LoanResponseDTO> pageLoan = new PageImpl<>(
                loanList,
                pageRequest,
                loanList.size()
        );
        Pageable pageable = PageRequest.of(0, 10);
        when(loanService.listLoansByStatus(LoanStatus.ACTIVE, pageable)).thenReturn(pageLoan);

        mockMvc.perform(get("/api/loans")
                .param("page", "0")
                .param("size", "10")
                .param("loanStatus", LoanStatus.ACTIVE.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3));
    }

    @Test
    void shouldListUsersWithOverdueStatus() throws Exception {

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                1L,
                "Andrey",
                "email@gmail.com",
                "+55 (11) 91234-5678"
        );

        UserResponseDTO userResponseDTO2 = new UserResponseDTO(
                2L,
                "Claude",
                "claude@gmail.com",
                "+55 (21) 98765-4321"
        );

        List<UserResponseDTO> userResponseList = List.of(userResponseDTO, userResponseDTO2);

        Pageable pageable = PageRequest.of(0,10);

        Page<UserResponseDTO> overdueUsers = new PageImpl<>(
                userResponseList,
                pageable,
                userResponseList.size()
        );

        when(loanService.listUsersWithOverdueLoans(LoanStatus.OVERDUE, pageable)).thenReturn(overdueUsers);

        mockMvc.perform(get("/api/loans/overdue-users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("loanStatus", LoanStatus.OVERDUE.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Andrey"));
    }

    @Test
    void shouldListMostBorrowedBooks() throws Exception {
        BookLoanCountProjection proj1 = new BookLoanCountProjection() {
            @Override
            public Long getBookId() {
                return 0L;
            }

            @Override
            public Long getQuantity() {
                return 10L;
            }
        };

        BookLoanCountProjection proj2 = new BookLoanCountProjection() {
            @Override
            public Long getBookId() {
                return 0L;
            }

            @Override
            public Long getQuantity() {
                return 5L;
            }
        };

        List<BookLoanCountProjection> projectionList = new ArrayList<>();
        projectionList.add(proj1);
        projectionList.add(proj2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<BookLoanCountProjection> loanPage = new PageImpl<>(
                projectionList,
                pageable,
                projectionList.size()
        );

        when(loanService.listMostBorrowedBooks(pageable)).thenReturn(loanPage);

        mockMvc.perform(get("/api/loans/most-borrowed")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].quantity").value(10L));
    }
}