package com.finance.management.Controller;

import com.finance.management.dto.ApiResponseDto;
import com.finance.management.entity.ExpenseEntity;
import com.finance.management.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/expenses")
    public ResponseEntity<ApiResponseDto<ExpenseEntity>> addExpense(
            @RequestParam("purpose") String purpose,
            @RequestParam("sum") BigDecimal sum,
            @RequestParam("date") LocalDate date,
            @RequestParam("category") String category,
            @RequestParam(value = "billsImage", required = false) MultipartFile billsImage){
        try {
            ExpenseEntity expense = expenseService.addExpenditure(purpose, sum, date, category, billsImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(true, expense, null, HttpStatus.CREATED.value(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }



}
