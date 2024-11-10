package com.finance.expense.Controller;

import com.finance.expense.dto.SavingsGoalDto;
import com.finance.expense.entity.SavingsGoal;
import com.finance.expense.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class SavingsController {
    @Autowired
    private SavingsGoalService savingsGoalService;

    @PostMapping("/set-goal")
    public ResponseEntity<SavingsGoal> setSavingsGoal(@RequestBody SavingsGoalDto savingsGoalDto) {
        SavingsGoal goal = savingsGoalService.setSavingsGoal(savingsGoalDto.getUserEmail(), savingsGoalDto.getGoalName(), savingsGoalDto.getTargetAmount(), savingsGoalDto.getDueDate());
        return ResponseEntity.ok(goal);
    }
    @GetMapping("/goals")
    public ResponseEntity<List<SavingsGoal>> getSavingsGoals(@RequestParam String userEmail) {
        List<SavingsGoal> goals = savingsGoalService.getSavingsGoals(userEmail);
        return ResponseEntity.ok(goals);
    }

    @PostMapping("/update-savings")
    public ResponseEntity<SavingsGoal> updateCurrentSavings(@RequestParam Long goalId,
                                                            @RequestParam BigDecimal additionalSavings,
                                                            @RequestParam LocalDate dueDate,
                                                            @RequestParam String goalName) {
        SavingsGoal updatedGoal = savingsGoalService.updateCurrentSavings(goalId, additionalSavings, dueDate, goalName);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/delete-savings")
    public ResponseEntity<String> deleteSavingsGoal(@RequestParam Long goalId) {
        try {
            savingsGoalService.deleteSavingsGoal(goalId);
            return ResponseEntity.ok("Savings goal deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete savings goal: " + e.getMessage());
        }
    }
}
