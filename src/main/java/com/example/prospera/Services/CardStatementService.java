package com.example.prospera.Services;

import com.example.prospera.DTO.CardStatementResponse;
import com.example.prospera.DTO.ExpenseInstallmentRecord;
import com.example.prospera.Entities.*;
import com.example.prospera.repositories.CardPaymentRepository;
import com.example.prospera.repositories.ExpenseInstallmentRepository;
import com.example.prospera.repositories.ExpenseRepository;
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
    private final CardPaymentRepository cardPaymentRepository;

    public CardStatementService(CardService cardService, AuthenticatedUserService authenticatedUserService,
                                ExpenseInstallmentRepository installmentRepository, ExpenseRepository expenseRepository,
                                CardPaymentRepository cardPaymentRepository) {
        this.cardService = cardService;
        this.authenticatedUserService = authenticatedUserService;
        this.installmentRepository = installmentRepository;
        this.expenseRepository = expenseRepository;
        this.cardPaymentRepository = cardPaymentRepository;
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
        BigDecimal paidAmount = zeroIfNull(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(user.getId(), cardId, month, year));
        BigDecimal remainingAmount = totalAmount.subtract(paidAmount);
        BigDecimal availableLimit = card.getCreditLimit().subtract(totalAmount);
        LocalDate dueDate = CardBillingCycleCalculator.atConfiguredDay(YearMonth.of(year, month), card.getDueDay());
        LocalDate closingDate = CardBillingCycleCalculator.closingDateForDueMonth(card, month, year);

        return new CardStatementResponse(card.getId(), card.getName(), month, year, dueDate, closingDate,
                totalAmount, availableLimit, paidAmount, remainingAmount, resolveStatus(totalAmount, paidAmount),
                installmentRecords);
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

    public static CardStatementStatus resolveStatus(BigDecimal totalAmount, BigDecimal paidAmount) {
        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            return CardStatementStatus.OPEN;
        }
        int comparison = paidAmount.compareTo(totalAmount);
        if (comparison < 0) {
            return CardStatementStatus.PARTIALLY_PAID;
        }
        if (comparison == 0) {
            return CardStatementStatus.PAID;
        }
        return CardStatementStatus.OVERPAID;
    }
}
