package com.finance.expense.service;

import com.finance.expense.entity.SavingsGoal;
import com.finance.expense.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
public class SavingsGoalService {
    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    public SavingsGoal setSavingsGoal(String userEmail, String goalName, BigDecimal targetAmount, LocalDate dueDate) {
        if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Target amount must be a positive value.");
        }

        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setUserEmail(userEmail);
        savingsGoal.setGoalName(goalName);
        savingsGoal.setTargetAmount(targetAmount);
        savingsGoal.setCurrentSavings(BigDecimal.ZERO);
        savingsGoal.setDueDate(dueDate);

        return savingsGoalRepository.save(savingsGoal);
    }

    public List<SavingsGoal> getSavingsGoals(String userEmail) {
        return savingsGoalRepository.findByUserEmail(userEmail);
    }

    public SavingsGoal updateCurrentSavings(Long goalId, BigDecimal additionalSavings, LocalDate dueDate, String goalName) {
        SavingsGoal savingsGoal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Savings goal not found"));

        savingsGoal.setTargetAmount(additionalSavings);
        savingsGoal.setGoalName(goalName);
        savingsGoal.setDueDate(dueDate);
        return savingsGoalRepository.save(savingsGoal);
    }

    public void deleteSavingsGoal(Long goalId) {
        savingsGoalRepository.deleteById(goalId);
    }
}