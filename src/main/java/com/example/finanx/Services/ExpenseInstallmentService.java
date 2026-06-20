package com.example.finanx.Services;

import com.example.finanx.Entities.Card;
import com.example.finanx.Entities.Expense;
import com.example.finanx.Entities.ExpenseInstallment;
import com.example.finanx.repositories.ExpenseInstallmentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public void generateInstallments(Expense expense, Card card) {
        List<ExpenseInstallment> installments = new ArrayList<>();
        BigDecimal totalAmount = expense.getAmount();
        int installmentCount = expense.getInstallmentCount();
        LocalDate purchaseDate = LocalDate.from(expense.getPurchaseDate());
        LocalDate firstDueDate = card == null
                ? purchaseDate
                : CardBillingCycleCalculator.firstDueDate(card, purchaseDate);

        BigDecimal installmentAmount = totalAmount.divide(BigDecimal.valueOf(installmentCount), 2, RoundingMode.HALF_UP);

        for (int i = 0; i < installmentCount; i++) {
            ExpenseInstallment installment = new ExpenseInstallment(
                    expense.getId(),
                    i + 1,
                    installmentAmount,
                    firstDueDate.plusMonths(i)
            );

            installments.add(installment);
        }

        installmentRepository.saveAll(installments);
    }

    public void deleteByExpenseId(Integer expenseId) {
        installmentRepository.deleteById_ExpenseId(expenseId);
    }
}
