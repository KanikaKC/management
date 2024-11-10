package com.finance.management.repository;

import com.finance.management.entity.ExpenseEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    List<ExpenseEntity> findByCategory(String category);

    List<ExpenseEntity> findByUserEmail(String userEmail);
}
