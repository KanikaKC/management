package com.finance.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDto {
    private String purpose;
    private BigDecimal sum;
    private LocalDate date;
}
