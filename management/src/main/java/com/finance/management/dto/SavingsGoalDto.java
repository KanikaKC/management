package com.finance.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalDto {
    private String userEmail;
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDate dueDate;
}
