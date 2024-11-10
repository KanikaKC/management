package com.finance.management.service;

import com.finance.management.dto.ExpenseCategory;
import com.finance.management.dto.ExpenseDto;
import com.finance.management.dto.IncomeDto;
import com.finance.management.dto.TransactionDto;
import com.finance.management.entity.ExpenseEntity;
import com.finance.management.entity.UserDetailsEntity;
import com.finance.management.repository.ExpenseRepository;
import com.finance.management.repository.UserManagementRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserManagementRepository userManagementRepository;

    public ExpenseEntity addIncome(IncomeDto incomeDto, String userEmail) {
        UserDetailsEntity user = userManagementRepository.findByEmailId(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found for email: " + userEmail);
        }
        BigDecimal sum = incomeDto.getSum();
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income sum must be a positive value.");
        }
        ExpenseEntity income = new ExpenseEntity();
        income.setPurpose(incomeDto.getPurpose());
        income.setCategory(ExpenseCategory.REVENUE);
        income.setSum(sum);
        income.setDate(incomeDto.getDate());
        income.setUser(user);
        return expenseRepository.save(income);
    }
    public ExpenseEntity addExpenditure(ExpenseDto expenseDto, String userEmail) {
        UserDetailsEntity user = userManagementRepository.findByEmailId(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found for email: " + userEmail);
        }
        BigDecimal totalBalance = getTotalBalance(userEmail);
        if (totalBalance.add(expenseDto.getSum().negate()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance to add this expenditure");
        }
        BigDecimal sum = expenseDto.getSum();
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income sum must be a positive value.");
        }
        ExpenseEntity expense = new ExpenseEntity();
        expense.setPurpose(expenseDto.getPurpose());
        expense.setCategory(ExpenseCategory.valueOf(expenseDto.getCategory()));
        expense.setSum(expenseDto.getSum().negate());
        expense.setDate(expenseDto.getDate());
        expense.setUser(user);
        return expenseRepository.save(expense);
    }
    public BigDecimal getTotalIncome(String userEmail) {
        return expenseRepository.findAll().stream()
                .filter(entry -> entry.getUser().getEmailId().equals(userEmail))
                .filter(entry -> entry.getSum().compareTo(BigDecimal.ZERO) > 0)
                .map(ExpenseEntity::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpenditure(String userEmail) {
        return expenseRepository.findAll().stream()
                .filter(entry -> entry.getUser().getEmailId().equals(userEmail))
                .filter(entry -> entry.getSum().compareTo(BigDecimal.ZERO) < 0)
                .map(ExpenseEntity::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalBalance(String userEmail) {
        BigDecimal totalIncome = getTotalIncome(userEmail);
        BigDecimal totalExpenditure = getTotalExpenditure(userEmail);
        return totalIncome.add(totalExpenditure);
    }

    public List<TransactionDto> getAllTransactions(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email must not be null or empty");
        }
        UserDetailsEntity user = userManagementRepository.findByEmailId(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + userEmail);
        }
        return expenseRepository.findAll().stream()
                .filter(expense -> expense.getUser().getEmailId().equals(userEmail))
                .map(expense -> new TransactionDto(
                        expense.getPurpose(),
                        expense.getCategory().name(),
                        expense.getSum(),
                        expense.getDate()))
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getRecentTransactions(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email must not be null or empty");
        }

        UserDetailsEntity user = userManagementRepository.findByEmailId(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + userEmail);
        }
        return expenseRepository.findAll().stream()
                .filter(expense -> expense.getUser().getEmailId().equals(userEmail))
                .sorted(Comparator.comparing(ExpenseEntity::getDate).reversed())
                .map(expense -> new TransactionDto(
                        expense.getPurpose(),
                        expense.getCategory().name(),
                        expense.getSum(),
                        expense.getDate()))
                .limit(20)
                .collect(Collectors.toList());
    }

}
