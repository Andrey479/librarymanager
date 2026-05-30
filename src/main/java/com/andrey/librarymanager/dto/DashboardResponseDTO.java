package com.andrey.librarymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {
    Long numberOfBooks;
    Long borrowedBooks;
    Long overdueLoans;
    BigDecimal finesValue;
}
