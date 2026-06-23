package com.example.prospera.Services;

import com.example.prospera.DTO.CardStatementResponse;
import com.example.prospera.Entities.*;
import com.example.prospera.repositories.CardPaymentRepository;
import com.example.prospera.repositories.ExpenseInstallmentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class CardStatementService {
    private final CardService cardService;
    private final AuthenticatedUserService authenticatedUserService;
    private final ExpenseInstallmentRepository installmentRepository;
    private final CardPaymentRepository cardPaymentRepository;

    public CardStatementService(CardService cardService, AuthenticatedUserService authenticatedUserService,
                                ExpenseInstallmentRepository installmentRepository,
                                CardPaymentRepository cardPaymentRepository) {
        this.cardService = cardService;
        this.authenticatedUserService = authenticatedUserService;
        this.installmentRepository = installmentRepository;
        this.cardPaymentRepository = cardPaymentRepository;
    }

    public CardStatementResponse getStatement(Integer cardId, Integer month, Integer year) {
        User user = authenticatedUserService.getAuthenticatedUser();
        validateMonthYear(month, year);

        Card card = cardService.findUserCard(user.getId(), cardId);
        YearMonth statementMonth = YearMonth.of(year, month);
        LocalDate from = statementMonth.atDay(1);
        LocalDate to = statementMonth.plusMonths(1).atDay(1);

        var installmentRecords = installmentRepository.findCardStatementInstallmentRecords(
                user.getId(), cardId, from, to);
        BigDecimal totalAmount = zeroIfNull(installmentRepository.sumCardStatementInDueDateRange(
                user.getId(), cardId, from, to));
        BigDecimal paidAmount = zeroIfNull(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(user.getId(), cardId, month, year));
        BigDecimal remainingAmount = totalAmount.subtract(paidAmount);
        BigDecimal availableLimit = card.getCreditLimit().subtract(totalAmount);
        LocalDate dueDate = CardBillingCycleCalculator.atConfiguredDay(statementMonth, card.getDueDay());
        LocalDate closingDate = CardBillingCycleCalculator.closingDateForDueMonth(card, month, year);

        return new CardStatementResponse(card.getId(), card.getName(), month, year, dueDate, closingDate,
                totalAmount, availableLimit, paidAmount, remainingAmount, resolveStatus(totalAmount, paidAmount),
                installmentRecords);
    }

    public CardStatementResponse getCurrentStatement(Integer cardId) {
        LocalDate today = LocalDate.now();
        return getStatement(cardId, today.getMonthValue(), today.getYear());
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
