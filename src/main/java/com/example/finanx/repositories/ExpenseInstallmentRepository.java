package com.example.finanx.Repositories;

import com.example.finanx.Entities.ExpenseInstallment;
import com.example.finanx.Entities.ExpenseInstallmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseInstallmentRepository extends JpaRepository <ExpenseInstallment, ExpenseInstallmentId> {
    List<ExpenseInstallment> findById_ExpenseId(Integer expenseId);
}
