package com.finance.management.Controller;

import com.finance.management.dto.ApiResponseDto;
import com.finance.management.dto.ExpenseDto;
import com.finance.management.dto.IncomeDto;
import com.finance.management.dto.TransactionDto;
import com.finance.management.entity.ExpenseEntity;
import com.finance.management.service.ExpenseService;
import com.finance.management.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/add-expense")
    public ResponseEntity<ApiResponseDto<ExpenseEntity>> addExpense(@RequestBody ExpenseDto expenseDto,
                                        @RequestHeader("User-Email") String userEmail) {
        try {
            ExpenseEntity expense = expenseService.addExpenditure(expenseDto, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(true, expense, null, HttpStatus.CREATED.value(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(),null));
        }
    }

    @PostMapping("/income")
    public ResponseEntity<ApiResponseDto<ExpenseEntity>> addIncome(@RequestBody IncomeDto incomeDto, @RequestHeader("User-Email") String userEmail) {
        try {
            ExpenseEntity income = expenseService.addIncome(incomeDto, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(true, income, null, HttpStatus.CREATED.value(),null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(),null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(),null));
        }
    }

    @GetMapping("/recent-transactions")
    public ResponseEntity<ApiResponseDto<List<TransactionDto>>> getRecentTransactions(@RequestParam String userEmail) {
        try {
            List<TransactionDto> transactions = expenseService.getRecentTransactions(userEmail);
            return ResponseEntity.ok(new ApiResponseDto<>(true, transactions, null, HttpStatus.OK.value(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(),null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(),null));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponseDto<List<TransactionDto>>> getAllTransactions(@RequestParam String userEmail) {
        try {
            List<TransactionDto> transactions = expenseService.getAllTransactions(userEmail);
            return ResponseEntity.ok(new ApiResponseDto<>(true, transactions, null, HttpStatus.OK.value(), null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(),null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(),null));
        }
    }
}
