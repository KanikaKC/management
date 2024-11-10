package com.finance.expense.service;

import com.finance.expense.dto.ExpenseCategory;
import com.finance.expense.dto.ExpenseDto;
import com.finance.expense.dto.IncomeDto;
import com.finance.expense.entity.ExpenseEntity;
import com.finance.expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;


    public ExpenseEntity addIncome(IncomeDto incomeDto, String userEmail) {

        BigDecimal sum = incomeDto.getSum();
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income sum must be a positive value.");
        }
        ExpenseEntity income = new ExpenseEntity();
        income.setPurpose(incomeDto.getPurpose());
        income.setCategory(ExpenseCategory.REVENUE);
        income.setSum(sum);
        income.setEmailId(userEmail);
        income.setDate(incomeDto.getDate());
        return expenseRepository.save(income);
    }
    public ExpenseEntity addExpenditure(String purpose, BigDecimal sum, LocalDate date, String category, MultipartFile billsImage, String userEmail) {
        BigDecimal totalBalance = getTotalBalance(userEmail);
        if (totalBalance.add(sum.negate()).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance to add this expenditure");
        }
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense sum must be a positive value.");
        }

        String imagePath = null;
        if (billsImage != null && !billsImage.isEmpty()) {
            imagePath = saveImage(billsImage);
        }

        ExpenseEntity expense = new ExpenseEntity();
        expense.setPurpose(purpose);
        expense.setEmailId(userEmail);
        expense.setCategory(ExpenseCategory.valueOf(category));
        expense.setSum(sum.negate());
        expense.setDate(date);
        expense.setBillsImage(imagePath);

        return expenseRepository.save(expense);
    }

    private String saveImage(MultipartFile file) {
        String folder = "/C:/Users/kzo1kor/Desktop/Image/Bills";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destinationFile = new File(folder + File.separator + fileName);

        try {
            file.transferTo(destinationFile); // Save the file
            return "/uploads/" + fileName; // Return the relative path
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
    }
    public BigDecimal getTotalIncome(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email must not be null or empty");
        }

        return expenseRepository.findAll().stream()
                .filter(entry -> entry.getEmailId().equals(userEmail) && entry.getSum().compareTo(BigDecimal.ZERO) > 0)
                .map(ExpenseEntity::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal getTotalExpenditure(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email must not be null or empty");
        }

        return expenseRepository.findAll().stream()
                .filter(entry -> entry.getEmailId().equals(userEmail) && entry.getSum().compareTo(BigDecimal.ZERO) < 0)
                .map(ExpenseEntity::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public BigDecimal getTotalBalance(String userEmail) {
        BigDecimal totalIncome = getTotalIncome(userEmail);
        BigDecimal totalExpenditure = getTotalExpenditure(userEmail);
        return totalIncome.add(totalExpenditure);
    }


    public List<ExpenseEntity> getAllTransactions(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email must not be null or empty");
        }
        return expenseRepository.findAll().stream()
                .filter(expense -> expense.getEmailId().equals(userEmail))
                .toList();
    }

    public void deleteTransactions(Long id) {

        expenseRepository.deleteById(id);
    }

    public ExpenseEntity updateExpense(Long id, ExpenseDto expenseDto, String userEmail) {
        Optional<ExpenseEntity> optionalExpense = expenseRepository.findById(id);
        if (!optionalExpense.isPresent()) {
            throw new IllegalArgumentException("Expense not found");
        }

        ExpenseEntity expense = optionalExpense.get();
        
        expense.setPurpose(expenseDto.getPurpose());
        expense.setCategory(ExpenseCategory.valueOf(expenseDto.getCategory()));
        expense.setSum(expenseDto.getSum());
        expense.setDate(expenseDto.getDate());
        
        return expenseRepository.save(expense);

    }

}
