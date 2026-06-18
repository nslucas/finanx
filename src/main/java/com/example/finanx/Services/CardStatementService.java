package com.example.finanx.Services;

import com.example.finanx.DTO.CardStatementResponse;
import com.example.finanx.DTO.ExpenseInstallmentRecord;
import com.example.finanx.Entities.Card;
import com.example.finanx.Entities.Expense;
import com.example.finanx.Entities.ExpenseInstallment;
import com.example.finanx.Entities.User;
import com.example.finanx.Repositories.ExpenseInstallmentRepository;
import com.example.finanx.Repositories.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class CardStatementService {
    private final CardService cardService;
    private final AuthenticatedUserService authenticatedUserService;
    private final ExpenseInstallmentRepository installmentRepository;
    private final ExpenseRepository expenseRepository;

    public CardStatementService(CardService cardService, AuthenticatedUserService authenticatedUserService,
                                ExpenseInstallmentRepository installmentRepository, ExpenseRepository expenseRepository) {
        this.cardService = cardService;
        this.authenticatedUserService = authenticatedUserService;
        this.installmentRepository = installmentRepository;
        this.expenseRepository = expenseRepository;
    }

    public CardStatementResponse getStatement(Integer cardId, Integer month, Integer year) {
        User user = authenticatedUserService.getAuthenticatedUser();
        validateMonthYear(month, year);

        Card card = cardService.findUserCard(user.getId(), cardId);
        List<ExpenseInstallment> installments = installmentRepository
                .findCardStatementInstallments(user.getId(), cardId, month, year);
        List<ExpenseInstallmentRecord> installmentRecords = installments.stream()
                .map(installment -> new ExpenseInstallmentRecord(installment, findExpense(installment.getExpenseId())))
                .toList();

        BigDecimal totalAmount = zeroIfNull(installmentRepository.sumCardStatement(user.getId(), cardId, month, year));
        BigDecimal availableLimit = card.getCreditLimit().subtract(totalAmount);
        LocalDate dueDate = CardBillingCycleCalculator.atConfiguredDay(YearMonth.of(year, month), card.getDueDay());
        LocalDate closingDate = CardBillingCycleCalculator.closingDateForDueMonth(card, month, year);

        return new CardStatementResponse(card.getId(), card.getName(), month, year, dueDate, closingDate,
                totalAmount, availableLimit, installmentRecords);
    }

    public CardStatementResponse getCurrentStatement(Integer cardId) {
        LocalDate today = LocalDate.now();
        return getStatement(cardId, today.getMonthValue(), today.getYear());
    }

    private Expense findExpense(Integer expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Installment points to a missing expense"));
    }

    private void validateMonthYear(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year == null || year < 1900) {
            throw new IllegalArgumentException("Year must be valid");
        }
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
