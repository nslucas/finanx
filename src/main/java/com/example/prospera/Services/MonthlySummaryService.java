package com.example.prospera.Services;

import com.example.prospera.DTO.MonthlySummaryResponse;
import com.example.prospera.Entities.Card;
import com.example.prospera.Entities.TransactionType;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.CardRepository;
import com.example.prospera.repositories.ExpenseInstallmentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonthlySummaryService {
    private final AuthenticatedUserService authenticatedUserService;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CardRepository cardRepository;
    private final ExpenseInstallmentRepository installmentRepository;
    private final CardPaymentService cardPaymentService;
    private final SpendingInsightService spendingInsightService;
    private final BudgetService budgetService;

    public MonthlySummaryService(AuthenticatedUserService authenticatedUserService, TransactionService transactionService,
                                 AccountService accountService, CardRepository cardRepository,
                                 ExpenseInstallmentRepository installmentRepository, CardPaymentService cardPaymentService,
                                 SpendingInsightService spendingInsightService, BudgetService budgetService) {
        this.authenticatedUserService = authenticatedUserService;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.cardRepository = cardRepository;
        this.installmentRepository = installmentRepository;
        this.cardPaymentService = cardPaymentService;
        this.spendingInsightService = spendingInsightService;
        this.budgetService = budgetService;
    }

    public MonthlySummaryResponse getMonthlySummary(Integer month, Integer year) {
        validateMonthYear(month, year);
        User user = authenticatedUserService.getAuthenticatedUser();

        BigDecimal incomeTotal = transactionService.sumByType(user.getId(), TransactionType.INCOME, month, year);
        BigDecimal accountExpenseTotal = transactionService.sumByType(user.getId(), TransactionType.EXPENSE, month, year);
        BigDecimal cardPaymentsTotal = transactionService.sumByType(user.getId(), TransactionType.CARD_PAYMENT, month, year);
        BigDecimal netCashFlow = incomeTotal.subtract(accountExpenseTotal).subtract(cardPaymentsTotal);
        BigDecimal totalAccountBalance = accountService.getTotalActiveBalance(user.getId());

        BigDecimal cardBillsTotal = BigDecimal.ZERO;
        BigDecimal cardBillsRemaining = BigDecimal.ZERO;
        YearMonth statementMonth = YearMonth.of(year, month);
        LocalDate from = statementMonth.atDay(1);
        LocalDate to = statementMonth.plusMonths(1).atDay(1);
        Map<Integer, BigDecimal> statementTotalsByCard = totalsByCard(
                installmentRepository.sumCardStatementsByCardInDueDateRange(user.getId(), from, to));
        Map<Integer, BigDecimal> paymentsByCard = totalsByCard(
                cardPaymentService.getPaidAmountsByCard(user.getId(), month, year));
        List<Card> cards = cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(user.getId());
        for (Card card : cards) {
            BigDecimal statementTotal = statementTotalsByCard.getOrDefault(card.getId(), BigDecimal.ZERO);
            BigDecimal paidAmount = paymentsByCard.getOrDefault(card.getId(), BigDecimal.ZERO);
            cardBillsTotal = cardBillsTotal.add(statementTotal);
            BigDecimal remaining = statementTotal.subtract(paidAmount);
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                cardBillsRemaining = cardBillsRemaining.add(remaining);
            }
        }

        return new MonthlySummaryResponse(month, year, incomeTotal, accountExpenseTotal, cardPaymentsTotal,
                netCashFlow, totalAccountBalance, cardBillsTotal, cardBillsRemaining,
                spendingInsightService.getCategorySummary(month, year),
                budgetService.getProgress(user.getId(), month, year));
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

    private Map<Integer, BigDecimal> totalsByCard(List<Object[]> rows) {
        Map<Integer, BigDecimal> totals = new HashMap<>();
        if (rows == null) {
            return totals;
        }
        for (Object[] row : rows) {
            totals.put((Integer) row[0], zeroIfNull((BigDecimal) row[1]));
        }
        return totals;
    }
}
