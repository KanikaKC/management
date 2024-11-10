package com.finance.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private String purpose;
    private String category;
    private BigDecimal sum;
    private LocalDate date;
}
