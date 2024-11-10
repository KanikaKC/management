package com.finance.management.service;

import com.finance.management.config.RabbitConfig;
import com.finance.management.dto.Category;
import com.finance.management.dto.IncomeDto;
import com.finance.management.entity.IncomeEntity;
import com.finance.management.repository.IncomeRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public IncomeEntity addIncome(IncomeDto incomeDto, String userEmail) {

        BigDecimal sum = incomeDto.getSum();
        if (sum.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Income sum must be a positive value.");
        }
        IncomeEntity income = new IncomeEntity();
        income.setPurpose(incomeDto.getPurpose());
        income.setCategory(Category.REVENUE);
        income.setSum(sum);
        income.setDate(incomeDto.getDate());

        IncomeEntity savedIncome = incomeRepository.save(income);
        rabbitTemplate.convertAndSend(RabbitConfig.INCOME_QUEUE, savedIncome);
        return savedIncome;
    }

}
