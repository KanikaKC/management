package com.finance.management.service;

import com.finance.management.config.RabbitConfig;
import com.finance.management.dto.TransactionDto;
import com.finance.management.entity.ExpenseEntity;
import com.finance.management.entity.IncomeEntity;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    private BigDecimal totalIncome = BigDecimal.ZERO;
    private BigDecimal totalExpenditure = BigDecimal.ZERO;
    private final List<TransactionDto> transactions = new ArrayList<>();

    @RabbitListener(queues = RabbitConfig.INCOME_QUEUE)
    public void handleIncome(IncomeEntity income) {
        totalIncome = totalIncome.add(income.getSum());
        transactions.add(new TransactionDto(income.getPurpose(), "INCOME", income.getSum(), income.getDate()));
        calculateBalance();
    }

    @RabbitListener(queues = RabbitConfig.EXPENSE_QUEUE)
    public void handleExpense(ExpenseEntity expense) {
        totalExpenditure = totalExpenditure.add(expense.getSum());
        transactions.add(new TransactionDto(expense.getPurpose(), "EXPENSE", expense.getSum(), expense.getDate()));
        calculateBalance();
    }

    public BigDecimal calculateBalance() {
        return totalIncome.subtract(totalExpenditure);
    }

    public List<TransactionDto> getAllTransactions() {
        return new ArrayList<>(transactions);
    }
}
