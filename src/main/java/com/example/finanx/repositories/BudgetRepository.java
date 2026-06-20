package com.example.finanx.repositories;

import com.example.finanx.Entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findByUserIdAndActiveTrueAndMonthAndYearOrderByCategoryIdAsc(Integer userId, Integer month,
                                                                               Integer year);

    Optional<Budget> findByIdAndUserId(Integer id, Integer userId);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYearAndActiveTrue(Integer userId, Integer categoryId,
                                                                            Integer month, Integer year);
}
