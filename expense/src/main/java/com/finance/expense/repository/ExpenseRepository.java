package com.finance.expense.repository;

import com.finance.expense.entity.ExpenseEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    List<ExpenseEntity> findByCategory(String category);

    void deleteByEmailId(String userEmail);
}
