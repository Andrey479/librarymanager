package com.andrey.librarymanager;

import com.andrey.librarymanager.controller.LoanController;
import com.andrey.librarymanager.dto.LoanRequestDTO;
import com.andrey.librarymanager.dto.LoanResponseDTO;
import com.andrey.librarymanager.model.Book;
import com.andrey.librarymanager.model.Loan;
import com.andrey.librarymanager.model.LoanStatus;
import com.andrey.librarymanager.model.User;
import com.andrey.librarymanager.service.LoanService;
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
                .andExpect(status().isBadRequest());;
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

        when(loanService.listLoansByStatus(LoanStatus.ACTIVE)).thenReturn(loanList);

        mockMvc.perform(get("/api/loans")
                .param("loanStatus", LoanStatus.ACTIVE.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }
}