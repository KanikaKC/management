package com.finance.management.Controller;

import com.finance.management.dto.ApiResponseDto;
import com.finance.management.dto.ExpenseDto;
import com.finance.management.entity.ExpenseEntity;
import com.finance.management.service.ExpenseService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private ExpenseService expenseService;



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
                                                                           @RequestBody ExpenseDto expenseDto) {
        try {
            ExpenseEntity updatedTransaction = expenseService.updateExpense(id, expenseDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, updatedTransaction, null, HttpStatus.OK.value(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }
}
