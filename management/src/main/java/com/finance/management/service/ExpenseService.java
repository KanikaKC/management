package com.finance.management.service;

import com.finance.management.config.RabbitConfig;
import com.finance.management.dto.*;
import com.finance.management.entity.ExpenseEntity;
import com.finance.management.entity.IncomeEntity;
import com.finance.management.repository.ExpenseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private String currentUserEmail;

    @RabbitListener(queues = RabbitConfig.USER_QUEUE)
    public void handleUserResponse(UserLoginDto userLoginDto) {
        currentUserEmail = userLoginDto.getEmailId();
        System.out.println("Received user email from RabbitMQ: " + currentUserEmail);
    }
    public ExpenseEntity addExpenditure(String purpose, BigDecimal sum, LocalDate date, String category, MultipartFile billsImage) {
        if (currentUserEmail == null) {
            throw new IllegalStateException("User email is not available. Ensure user login is successful and email is received from RabbitMQ.");
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
        expense.setCategory(Category.valueOf(category));
        expense.setSum(sum);
        expense.setDate(date);
        expense.setBillsImage(imagePath);
        expense.setUserEmail(currentUserEmail);

        ExpenseEntity savedExpense = expenseRepository.save(expense);
        rabbitTemplate.convertAndSend(RabbitConfig.EXPENSE_QUEUE, savedExpense);
        return savedExpense;
    }
    private String saveImage(MultipartFile file) {
        String folder = "/C:/Users/kzo1kor/Desktop/Image/Bills";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destinationFile = new File(folder + File.separator + fileName);

        try {
            file.transferTo(destinationFile);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
    }


    public List<ExpenseEntity> getAllTransactions(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email must not be null or empty");
        }
        return expenseRepository.findAll().stream()
                .filter(expense -> expense.getUserEmail().equals(userEmail))
                .toList();
    }

    public void deleteTransactions(Long id) {

        expenseRepository.deleteById(id);
    }

    public ExpenseEntity updateExpense(Long id, ExpenseDto expenseDto) {
        Optional<ExpenseEntity> optionalExpense = expenseRepository.findById(id);
        if (!optionalExpense.isPresent()) {
            throw new IllegalArgumentException("Expense not found");
        }

        ExpenseEntity expense = optionalExpense.get();
        
        expense.setPurpose(expenseDto.getPurpose());
        expense.setCategory(Category.valueOf(expenseDto.getCategory()));
        expense.setSum(expenseDto.getSum());
        expense.setDate(expenseDto.getDate());
        
        return expenseRepository.save(expense);

    }

}
