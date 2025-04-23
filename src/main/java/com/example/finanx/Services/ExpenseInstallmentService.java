package com.example.finanx.Services;

import com.example.finanx.Entities.Expense;
import com.example.finanx.Entities.ExpenseInstallment;
import com.example.finanx.Repositories.ExpenseInstallmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExpenseInstallmentService {
    private final ExpenseInstallmentRepository installmentRepository;

    public ExpenseInstallmentService(ExpenseInstallmentRepository installmentRepository) {
        this.installmentRepository = installmentRepository;
    }

    public void generateInstallments(Expense expense) {
        List<ExpenseInstallment> installments = new ArrayList<>();
        double totalAmount = expense.getAmount();
        int installmentCount = expense.getInstallmentCount();
        LocalDate firstDueDate = LocalDate.from(expense.getPurchaseDate()); // Certifique-se que este método existe na Expense

        double installmentAmount = totalAmount / installmentCount;
        installmentAmount = Math.round(installmentAmount * 100.0) / 100.0;

        for (int i = 0; i < installmentCount; i++) {
            ExpenseInstallment installment = new ExpenseInstallment(
                    expense.getId(), // expenseId
                    i + 1,         // installmentNumber
                    installmentAmount,
                    firstDueDate.plusMonths(i)
            );

            installments.add(installment);
        }

        installmentRepository.saveAll(installments);
    }
}