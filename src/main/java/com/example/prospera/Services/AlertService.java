package com.example.prospera.Services;

import com.example.prospera.DTO.AlertRecord;
import com.example.prospera.DTO.BudgetProgressRecord;
import com.example.prospera.Entities.*;
import com.example.prospera.repositories.AccountRepository;
import com.example.prospera.repositories.CardPaymentRepository;
import com.example.prospera.repositories.CardRepository;
import com.example.prospera.repositories.ExpenseInstallmentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AlertService {
    private static final BigDecimal PERCENT_MULTIPLIER = BigDecimal.valueOf(100);
    private static final BigDecimal CARD_LIMIT_PERCENT = BigDecimal.valueOf(80);
    private static final BigDecimal LOW_ACCOUNT_BALANCE_THRESHOLD = BigDecimal.valueOf(100);
    private static final int DEFAULT_DUE_WINDOW_DAYS = 7;

    private final AuthenticatedUserService authenticatedUserService;
    private final BudgetService budgetService;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final ExpenseInstallmentRepository installmentRepository;
    private final CardPaymentRepository cardPaymentRepository;
    private final Clock clock;

    public AlertService(AuthenticatedUserService authenticatedUserService, BudgetService budgetService,
                        CardRepository cardRepository, AccountRepository accountRepository,
                        ExpenseInstallmentRepository installmentRepository,
                        CardPaymentRepository cardPaymentRepository, Clock clock) {
        this.authenticatedUserService = authenticatedUserService;
        this.budgetService = budgetService;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.installmentRepository = installmentRepository;
        this.cardPaymentRepository = cardPaymentRepository;
        this.clock = clock;
    }

    public List<AlertRecord> getAlerts(Integer month, Integer year, LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now(clock);
        Integer alertMonth = month == null ? today.getMonthValue() : month;
        Integer alertYear = year == null ? today.getYear() : year;
        LocalDate windowStart = from == null ? today : from;
        LocalDate windowEnd = to == null ? windowStart.plusDays(DEFAULT_DUE_WINDOW_DAYS) : to;
        validate(alertMonth, alertYear, windowStart, windowEnd);

        User user = authenticatedUserService.getAuthenticatedUser();
        List<AlertRecord> alerts = new ArrayList<>();
        alerts.addAll(getBudgetAlerts(user.getId(), alertMonth, alertYear));
        alerts.addAll(getCardAlerts(user.getId(), alertMonth, alertYear, today, windowStart, windowEnd));
        alerts.addAll(getAccountAlerts(user.getId(), alertMonth, alertYear));
        Comparator<AlertRecord> alertComparator = Comparator
                .comparingInt((AlertRecord alert) -> severityRank(alert.severity()))
                .thenComparing(alert -> alert.dueDate() == null ? LocalDate.MAX : alert.dueDate())
                .thenComparing(AlertRecord::type)
                .thenComparing(AlertRecord::key);

        return alerts.stream()
                .sorted(alertComparator)
                .toList();
    }

    private List<AlertRecord> getBudgetAlerts(Integer userId, Integer month, Integer year) {
        return budgetService.getProgress(userId, month, year).stream()
                .filter(progress -> progress.status() == BudgetStatus.NEAR_LIMIT
                        || progress.status() == BudgetStatus.OVER_BUDGET)
                .map(progress -> {
                    boolean exceeded = progress.status() == BudgetStatus.OVER_BUDGET;
                    AlertType type = exceeded ? AlertType.BUDGET_EXCEEDED : AlertType.BUDGET_NEAR_LIMIT;
                    AlertSeverity severity = exceeded ? AlertSeverity.CRITICAL : AlertSeverity.WARNING;
                    BigDecimal threshold = exceeded
                            ? progress.budgetAmount()
                            : percentOf(progress.budgetAmount(), BigDecimal.valueOf(80));
                    return new AlertRecord(
                            key(type, "budget", progress.budgetId(), month, year),
                            type,
                            severity,
                            "Budget for " + progress.categoryName() + " is at "
                                    + progress.percentUsed().setScale(2, RoundingMode.HALF_UP) + "%.",
                            "BUDGET",
                            progress.budgetId(),
                            progress.spentAmount(),
                            threshold,
                            progress.percentUsed(),
                            null,
                            month,
                            year);
                })
                .toList();
    }

    private List<AlertRecord> getCardAlerts(Integer userId, Integer month, Integer year, LocalDate today,
                                            LocalDate windowStart, LocalDate windowEnd) {
        List<AlertRecord> alerts = new ArrayList<>();
        for (Card card : cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(userId)) {
            BigDecimal total = zeroIfNull(installmentRepository.sumCardStatement(userId, card.getId(), month, year));
            BigDecimal paid = zeroIfNull(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(userId, card.getId(), month, year));
            BigDecimal remaining = total.subtract(paid);
            addCardLimitAlert(alerts, card, total, month, year);
            addCardBillAlert(alerts, card, remaining, month, year, today, windowStart, windowEnd);
        }
        return alerts;
    }

    private void addCardLimitAlert(List<AlertRecord> alerts, Card card, BigDecimal total, Integer month, Integer year) {
        BigDecimal creditLimit = zeroIfNull(card.getCreditLimit());
        if (creditLimit.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal percentageUsed = total.multiply(PERCENT_MULTIPLIER)
                .divide(creditLimit, 2, RoundingMode.HALF_UP);
        if (percentageUsed.compareTo(CARD_LIMIT_PERCENT) < 0) {
            return;
        }
        alerts.add(new AlertRecord(
                key(AlertType.CARD_LIMIT_NEAR, "card", card.getId(), month, year),
                AlertType.CARD_LIMIT_NEAR,
                AlertSeverity.WARNING,
                "Card " + card.getName() + " reached "
                        + percentageUsed.setScale(2, RoundingMode.HALF_UP) + "% of its limit.",
                "CARD",
                card.getId(),
                total,
                percentOf(creditLimit, CARD_LIMIT_PERCENT),
                percentageUsed,
                null,
                month,
                year));
    }

    private void addCardBillAlert(List<AlertRecord> alerts, Card card, BigDecimal remaining, Integer month,
                                  Integer year, LocalDate today, LocalDate windowStart, LocalDate windowEnd) {
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        LocalDate dueDate = CardBillingCycleCalculator.atConfiguredDay(YearMonth.of(year, month), card.getDueDay());
        if (dueDate.isBefore(today)) {
            alerts.add(new AlertRecord(
                    key(AlertType.CARD_BILL_OVERDUE, "card", card.getId(), month, year),
                    AlertType.CARD_BILL_OVERDUE,
                    AlertSeverity.CRITICAL,
                    "Card " + card.getName() + " has an overdue bill.",
                    "CARD",
                    card.getId(),
                    remaining,
                    BigDecimal.ZERO,
                    null,
                    dueDate,
                    month,
                    year));
            return;
        }
        if (!dueDate.isBefore(windowStart) && !dueDate.isAfter(windowEnd)) {
            alerts.add(new AlertRecord(
                    key(AlertType.CARD_BILL_DUE_SOON, "card", card.getId(), month, year),
                    AlertType.CARD_BILL_DUE_SOON,
                    AlertSeverity.WARNING,
                    "Card " + card.getName() + " bill is due soon.",
                    "CARD",
                    card.getId(),
                    remaining,
                    BigDecimal.ZERO,
                    null,
                    dueDate,
                    month,
                    year));
        }
    }

    private List<AlertRecord> getAccountAlerts(Integer userId, Integer month, Integer year) {
        return accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(userId).stream()
                .filter(account -> zeroIfNull(account.getBalance()).compareTo(LOW_ACCOUNT_BALANCE_THRESHOLD) < 0)
                .map(account -> new AlertRecord(
                        key(AlertType.LOW_ACCOUNT_BALANCE, "account", account.getId(), month, year),
                        AlertType.LOW_ACCOUNT_BALANCE,
                        AlertSeverity.WARNING,
                        "Account " + account.getName() + " has a low balance.",
                        "ACCOUNT",
                        account.getId(),
                        zeroIfNull(account.getBalance()),
                        LOW_ACCOUNT_BALANCE_THRESHOLD,
                        null,
                        null,
                        month,
                        year))
                .toList();
    }

    private void validate(Integer month, Integer year, LocalDate from, LocalDate to) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year == null || year < 1900) {
            throw new IllegalArgumentException("Year must be valid");
        }
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Alert window end date must not be before start date");
        }
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal percentOf(BigDecimal amount, BigDecimal percent) {
        return amount.multiply(percent).divide(PERCENT_MULTIPLIER, 2, RoundingMode.HALF_UP);
    }

    private String key(AlertType type, String resourceType, Integer resourceId, Integer month, Integer year) {
        return type + ":" + resourceType + ":" + resourceId + ":" + year + "-" + month;
    }

    private int severityRank(AlertSeverity severity) {
        return severity == AlertSeverity.CRITICAL ? 0 : 1;
    }
}
