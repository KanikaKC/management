package com.finance.management.Controller;

import com.finance.management.dto.ApiResponseDto;
import com.finance.management.dto.IncomeDto;
import com.finance.management.entity.IncomeEntity;
import com.finance.management.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncomeController {

    @Autowired
    private IncomeService incomeService;


    @PostMapping("/income")
    public ResponseEntity<ApiResponseDto<IncomeEntity>> addIncome(@RequestBody IncomeDto incomeDto, @RequestHeader("User-Email") String userEmail) {
        try {
            IncomeEntity income = incomeService.addIncome(incomeDto, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(true, income, null, HttpStatus.CREATED.value(),null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, null, e.getMessage(), HttpStatus.BAD_REQUEST.value(),null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, null, "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR.value(),null));
        }
    }
}
