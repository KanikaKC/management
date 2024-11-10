package com.finance.management.entity;

import com.finance.management.dto.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "[income]")
public class IncomeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(precision = 10, scale = 2 , nullable = false)
    private BigDecimal sum;

    @Column(nullable = false)
    private LocalDate date;

    @Column
    private String billsImage;

    @Column(name = "user_mail_id", nullable = false)
    private String userEmail;

}

