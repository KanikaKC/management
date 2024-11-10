package com.finance.expense.Controller;

import com.finance.expense.dto.ApiResponseDto;
import com.finance.expense.dto.ExpenseDto;
import com.finance.expense.dto.IncomeDto;
import com.finance.expense.dto.TransactionDto;
import com.finance.expense.entity.ExpenseEntity;
import com.finance.expense.service.ExpenseService;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
            @RequestParam(value = "billsImage", required = false) MultipartFile billsImage,
            @RequestHeader("User-Email") String userEmail) {
        try {
            ExpenseEntity expense = expenseService.addExpenditure(purpose, sum, date, category, billsImage, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(true, expense, null, HttpStatus.CREATED.value(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
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

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponseDto<List<ExpenseEntity>>> getAllTransactions(@RequestParam String userEmail) {
        try {
            List<ExpenseEntity> transactions = expenseService.getAllTransactions(userEmail);
            return ResponseEntity.ok(new ApiResponseDto<>(true, transactions, null, HttpStatus.OK.value(), null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(),null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(),null));
        }
    }

    @GetMapping("/total-income")
    public ResponseEntity<BigDecimal> getTotalIncome(@RequestParam String userEmail) {
        try {
            BigDecimal totalIncome = expenseService.getTotalIncome(userEmail);
            return ResponseEntity.ok(totalIncome);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/total-expense")
    public ResponseEntity<BigDecimal> getTotalExpenditure(@RequestParam String userEmail) {
        try {
            BigDecimal totalExpense = expenseService.getTotalExpenditure(userEmail);
            return ResponseEntity.ok(totalExpense);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getCurrentBalance(@RequestParam String userEmail) {
        BigDecimal balance = expenseService.getTotalBalance(userEmail);
        return ResponseEntity.ok(balance);
    }
    @GetMapping("/export-transactions")
    public ResponseEntity<byte[]> exportTransactions(@RequestParam String userEmail) {
        try {
            List<ExpenseEntity> transactions = expenseService.getAllTransactions(userEmail);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Transactions");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Purpose");
            header.createCell(1).setCellValue("Category");
            header.createCell(2).setCellValue("Amount");
            header.createCell(3).setCellValue("Date");

            for (int i = 0; i < transactions.size(); i++) {
                ExpenseEntity transaction = transactions.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(transaction.getPurpose());
                row.createCell(1).setCellValue(transaction.getCategory().ordinal());
                row.createCell(2).setCellValue(transaction.getSum().doubleValue());
                row.createCell(3).setCellValue(transaction.getDate().toString());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            byte[] excelFile = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=transactions.xlsx");
            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/delete-transaction")
    public ResponseEntity<String> deleteTransaction(@RequestParam Long id) {
        try {
            expenseService.deleteTransactions(id);
            return ResponseEntity.ok("Transaction deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete transaction: " + e.getMessage());
        }
    }

    @PutMapping("/update-transaction/{id}")
    public ResponseEntity<ApiResponseDto<ExpenseEntity>> updateTransaction(@PathVariable Long id,
                                                                            @RequestBody ExpenseDto expenseDto, @RequestHeader("User-Email") String userEmail) {
        try {
            ExpenseEntity updatedTransaction = expenseService.updateExpense(id, expenseDto, userEmail);
            return ResponseEntity.ok(new ApiResponseDto<>(true, updatedTransaction, null, HttpStatus.OK.value(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

}
