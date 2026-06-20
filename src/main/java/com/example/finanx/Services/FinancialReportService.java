package com.example.finanx.Services;

import com.example.finanx.DTO.*;
import com.example.finanx.Entities.*;
import com.example.finanx.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class FinancialReportService {
    private final AuthenticatedUserService authenticatedUserService;
    private final RecurringTransactionService recurrenceService;
    private final RecurringOccurrenceRepository occurrenceRepository;
    private final CardRepository cardRepository;
    private final CardPaymentRepository cardPaymentRepository;
    private final ExpenseInstallmentRepository installmentRepository;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public FinancialReportService(AuthenticatedUserService authenticatedUserService,
                                  RecurringTransactionService recurrenceService,
                                  RecurringOccurrenceRepository occurrenceRepository,
                                  CardRepository cardRepository,
                                  CardPaymentRepository cardPaymentRepository,
                                  ExpenseInstallmentRepository installmentRepository,
                                  TransactionRepository transactionRepository,
                                  AccountService accountService) {
        this.authenticatedUserService = authenticatedUserService;
        this.recurrenceService = recurrenceService;
        this.occurrenceRepository = occurrenceRepository;
        this.cardRepository = cardRepository;
        this.cardPaymentRepository = cardPaymentRepository;
        this.installmentRepository = installmentRepository;
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    public UpcomingSummaryResponse getUpcoming(LocalDate from, LocalDate to) {
        validateRange(from, to);
        User user = authenticatedUserService.getAuthenticatedUser();
        List<RecurringOccurrenceRecord> occurrences = recurrenceService.previewOccurrences(user.getId(), from, to);
        List<UpcomingCardStatementRecord> statements = getUpcomingCardStatements(user.getId(), from, to);
        return new UpcomingSummaryResponse(from, to, occurrences, statements);
    }

    public ForecastResponse getForecast(Integer requestedMonths) {
        User user = authenticatedUserService.getAuthenticatedUser();
        int months = requestedMonths == null ? 12 : requestedMonths;
        if (months < 1 || months > 24) {
            throw new IllegalArgumentException("Forecast months must be between 1 and 24");
        }

        YearMonth start = YearMonth.now();
        YearMonth end = start.plusMonths(months - 1L);
        Map<YearMonth, ForecastAccumulator> pending = buildPendingRecurrenceForecast(user.getId(), start, end);
        BigDecimal projectedBalance = accountService.getTotalActiveBalance(user.getId());
        List<ForecastMonthRecord> forecast = new ArrayList<>();

        YearMonth cursor = start;
        while (!cursor.isAfter(end)) {
            Integer month = cursor.getMonthValue();
            Integer year = cursor.getYear();
            BigDecimal incomeTotal = sumByType(user.getId(), TransactionType.INCOME, month, year);
            BigDecimal accountExpenseTotal = sumByType(user.getId(), TransactionType.EXPENSE, month, year);
            BigDecimal cardStatementExpenseTotal = zeroIfNull(
                    installmentRepository.sumCardStatementByUserIdAndDueMonth(user.getId(), month, year));
            BigDecimal cardPaymentsTotal = zeroIfNull(
                    cardPaymentRepository.sumByUserIdAndMonthYear(user.getId(), month, year));
            ForecastAccumulator recurring = pending.getOrDefault(cursor, new ForecastAccumulator());

            BigDecimal projectedIncome = incomeTotal.add(recurring.income);
            BigDecimal projectedAccountExpense = accountExpenseTotal.add(recurring.accountExpense);
            BigDecimal projectedCardStatementExpense = cardStatementExpenseTotal.add(recurring.cardExpense);
            BigDecimal projectedNetCashFlow = projectedIncome.subtract(projectedAccountExpense).subtract(cardPaymentsTotal);
            projectedBalance = projectedBalance.add(projectedNetCashFlow);

            forecast.add(new ForecastMonthRecord(month, year, incomeTotal, accountExpenseTotal,
                    cardStatementExpenseTotal, cardPaymentsTotal, recurring.income, recurring.accountExpense,
                    recurring.cardExpense, projectedNetCashFlow, projectedBalance));
            cursor = cursor.plusMonths(1);
        }
        return new ForecastResponse(months, forecast);
    }

    public YearlySummaryResponse getYearlySummary(Integer year) {
        validateYear(year);
        User user = authenticatedUserService.getAuthenticatedUser();
        List<MonthlyTrendRecord> months = new ArrayList<>();
        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal accountExpenseTotal = BigDecimal.ZERO;
        BigDecimal cardStatementExpenseTotal = BigDecimal.ZERO;
        BigDecimal netTotal = BigDecimal.ZERO;

        for (int month = 1; month <= 12; month++) {
            BigDecimal income = sumByType(user.getId(), TransactionType.INCOME, month, year);
            BigDecimal accountExpense = sumByType(user.getId(), TransactionType.EXPENSE, month, year);
            BigDecimal cardExpense = zeroIfNull(
                    installmentRepository.sumCardStatementByUserIdAndDueMonth(user.getId(), month, year));
            BigDecimal net = income.subtract(accountExpense).subtract(cardExpense);
            months.add(new MonthlyTrendRecord(month, year, income, accountExpense, cardExpense, net));
            incomeTotal = incomeTotal.add(income);
            accountExpenseTotal = accountExpenseTotal.add(accountExpense);
            cardStatementExpenseTotal = cardStatementExpenseTotal.add(cardExpense);
            netTotal = netTotal.add(net);
        }
        return new YearlySummaryResponse(year, months, incomeTotal, accountExpenseTotal,
                cardStatementExpenseTotal, netTotal);
    }

    public List<CardMonthlySummaryRecord> getCardsSummary(Integer month, Integer year) {
        validateMonthYear(month, year);
        User user = authenticatedUserService.getAuthenticatedUser();
        return cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(user.getId()).stream()
                .map(card -> toCardSummary(user.getId(), card, month, year))
                .toList();
    }

    public FixedVariableSummaryResponse getFixedVariableSummary(Integer month, Integer year) {
        validateMonthYear(month, year);
        User user = authenticatedUserService.getAuthenticatedUser();
        BigDecimal fixed = materializedByClassification(user.getId(), RecurringClassification.FIXED, month, year);
        BigDecimal variable = materializedByClassification(user.getId(), RecurringClassification.VARIABLE, month, year);
        BigDecimal totalSpending = sumByType(user.getId(), TransactionType.EXPENSE, month, year)
                .add(zeroIfNull(installmentRepository.sumCardStatementByUserIdAndDueMonth(user.getId(), month, year)));
        BigDecimal unclassified = totalSpending.subtract(fixed).subtract(variable);
        if (unclassified.compareTo(BigDecimal.ZERO) < 0) {
            unclassified = BigDecimal.ZERO;
        }
        return new FixedVariableSummaryResponse(month, year, fixed, variable, unclassified);
    }

    private List<UpcomingCardStatementRecord> getUpcomingCardStatements(Integer userId, LocalDate from, LocalDate to) {
        List<UpcomingCardStatementRecord> statements = new ArrayList<>();
        YearMonth cursor = YearMonth.from(from);
        YearMonth end = YearMonth.from(to);
        List<Card> cards = cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(userId);
        while (!cursor.isAfter(end)) {
            for (Card card : cards) {
                LocalDate dueDate = CardBillingCycleCalculator.atConfiguredDay(cursor, card.getDueDay());
                if (dueDate.isBefore(from) || dueDate.isAfter(to)) {
                    continue;
                }
                BigDecimal total = zeroIfNull(installmentRepository.sumCardStatement(userId, card.getId(),
                        cursor.getMonthValue(), cursor.getYear()));
                BigDecimal paid = zeroIfNull(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(userId, card.getId(),
                        cursor.getMonthValue(), cursor.getYear()));
                BigDecimal remaining = total.subtract(paid);
                if (total.compareTo(BigDecimal.ZERO) > 0 || remaining.compareTo(BigDecimal.ZERO) > 0) {
                    statements.add(new UpcomingCardStatementRecord(card.getId(), card.getName(),
                            cursor.getMonthValue(), cursor.getYear(), dueDate,
                            CardBillingCycleCalculator.closingDateForDueMonth(card, cursor.getMonthValue(), cursor.getYear()),
                            total, paid, remaining, CardStatementService.resolveStatus(total, paid)));
                }
            }
            cursor = cursor.plusMonths(1);
        }
        return statements;
    }

    private Map<YearMonth, ForecastAccumulator> buildPendingRecurrenceForecast(Integer userId, YearMonth start,
                                                                               YearMonth end) {
        Map<YearMonth, ForecastAccumulator> totals = new HashMap<>();
        LocalDate from = start.atDay(1);
        LocalDate to = end.atEndOfMonth();
        for (RecurringTransaction recurrence : recurrenceService.findActiveUserRecurrencesInRange(userId, from, to)) {
            for (LocalDate date : recurrenceService.generateOccurrenceDates(recurrence, from, to)) {
                RecurringOccurrence occurrence = occurrenceRepository
                        .findByRecurrenceIdAndOccurrenceDate(recurrence.getId(), date)
                        .orElse(null);
                if (occurrence != null && occurrence.getStatus() != RecurringOccurrenceStatus.PENDING) {
                    continue;
                }
                if (recurrence.getTargetType() == RecurringTargetType.ACCOUNT_TRANSACTION) {
                    YearMonth month = YearMonth.from(date);
                    ForecastAccumulator accumulator = totals.computeIfAbsent(month, ignored -> new ForecastAccumulator());
                    if (recurrence.getTransactionType() == TransactionType.INCOME) {
                        accumulator.income = accumulator.income.add(recurrence.getAmount());
                    } else {
                        accumulator.accountExpense = accumulator.accountExpense.add(recurrence.getAmount());
                    }
                } else {
                    addProjectedCardInstallments(userId, totals, recurrence, date, start, end);
                }
            }
        }
        return totals;
    }

    private void addProjectedCardInstallments(Integer userId, Map<YearMonth, ForecastAccumulator> totals,
                                              RecurringTransaction recurrence, LocalDate purchaseDate,
                                              YearMonth start, YearMonth end) {
        Card card = cardRepository.findByIdAndUserId(recurrence.getCardId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("Recurring card not found"));
        LocalDate firstDueDate = CardBillingCycleCalculator.firstDueDate(card, purchaseDate);
        BigDecimal installmentAmount = recurrence.getAmount()
                .divide(BigDecimal.valueOf(recurrence.getInstallmentCount()), 2, RoundingMode.HALF_UP);
        for (int i = 0; i < recurrence.getInstallmentCount(); i++) {
            YearMonth dueMonth = YearMonth.from(firstDueDate.plusMonths(i));
            if (dueMonth.isBefore(start) || dueMonth.isAfter(end)) {
                continue;
            }
            ForecastAccumulator accumulator = totals.computeIfAbsent(dueMonth, ignored -> new ForecastAccumulator());
            accumulator.cardExpense = accumulator.cardExpense.add(installmentAmount);
        }
    }

    private CardMonthlySummaryRecord toCardSummary(Integer userId, Card card, Integer month, Integer year) {
        BigDecimal total = zeroIfNull(installmentRepository.sumCardStatement(userId, card.getId(), month, year));
        BigDecimal paid = zeroIfNull(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(userId, card.getId(), month, year));
        BigDecimal remaining = total.subtract(paid);
        return new CardMonthlySummaryRecord(card.getId(), card.getName(), month, year, total, paid, remaining,
                CardStatementService.resolveStatus(total, paid));
    }

    private BigDecimal materializedByClassification(Integer userId, RecurringClassification classification,
                                                    Integer month, Integer year) {
        BigDecimal account = zeroIfNull(occurrenceRepository.sumMaterializedAccountExpenseByClassification(userId,
                classification, RecurringOccurrenceStatus.MATERIALIZED, TransactionType.EXPENSE, month, year));
        BigDecimal card = zeroIfNull(occurrenceRepository.sumMaterializedCardExpenseByClassification(userId,
                classification, RecurringOccurrenceStatus.MATERIALIZED, month, year));
        return account.add(card);
    }

    private BigDecimal sumByType(Integer userId, TransactionType type, Integer month, Integer year) {
        return zeroIfNull(transactionRepository.sumByUserIdTypeAndMonth(userId, type, month, year));
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("From and to dates are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before or equal to to date");
        }
    }

    private void validateMonthYear(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        validateYear(year);
    }

    private void validateYear(Integer year) {
        if (year == null || year < 1900) {
            throw new IllegalArgumentException("Year must be valid");
        }
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static class ForecastAccumulator {
        private BigDecimal income = BigDecimal.ZERO;
        private BigDecimal accountExpense = BigDecimal.ZERO;
        private BigDecimal cardExpense = BigDecimal.ZERO;
    }
}
