package com.example.prospera.Services;

import com.example.prospera.DTO.AlertRecord;
import com.example.prospera.DTO.BudgetProgressRecord;
import com.example.prospera.Entities.*;
import com.example.prospera.repositories.AccountRepository;
import com.example.prospera.repositories.CardPaymentRepository;
import com.example.prospera.repositories.CardRepository;
import com.example.prospera.repositories.ExpenseInstallmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-06-18T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));

    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private BudgetService budgetService;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ExpenseInstallmentRepository installmentRepository;
    @Mock
    private CardPaymentRepository cardPaymentRepository;

    @Test
    void cardLimitAlertAppearsAtEightyPercentUsage() {
        mockUser();
        Card card = card(10, "Nubank", BigDecimal.valueOf(1000), 25);
        when(cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(1)).thenReturn(List.of(card));
        when(installmentRepository.sumCardStatement(1, 10, 6, 2026)).thenReturn(BigDecimal.valueOf(800));
        when(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(1, 10, 6, 2026)).thenReturn(BigDecimal.ZERO);
        when(budgetService.getProgress(1, 6, 2026)).thenReturn(List.of());
        when(accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(1)).thenReturn(List.of());

        List<AlertRecord> alerts = service().getAlerts(6, 2026,
                LocalDate.of(2026, 6, 18), LocalDate.of(2026, 6, 25));

        AlertRecord alert = findAlert(alerts, AlertType.CARD_LIMIT_NEAR);
        assertEquals(AlertSeverity.WARNING, alert.severity());
        assertEquals(BigDecimal.valueOf(800), alert.amount());
        assertEquals(new BigDecimal("80.00"), alert.percentageUsed());
    }

    @Test
    void cardLimitAlertDoesNotAppearBelowEightyPercentUsage() {
        mockUser();
        Card card = card(10, "Nubank", BigDecimal.valueOf(1000), 25);
        when(cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(1)).thenReturn(List.of(card));
        when(installmentRepository.sumCardStatement(1, 10, 6, 2026)).thenReturn(BigDecimal.valueOf(799));
        when(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(1, 10, 6, 2026)).thenReturn(BigDecimal.ZERO);
        when(budgetService.getProgress(1, 6, 2026)).thenReturn(List.of());
        when(accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(1)).thenReturn(List.of());

        List<AlertRecord> alerts = service().getAlerts(6, 2026,
                LocalDate.of(2026, 6, 18), LocalDate.of(2026, 6, 25));

        assertFalse(hasAlert(alerts, AlertType.CARD_LIMIT_NEAR));
    }

    @Test
    void budgetAlertsMapNearLimitAndExceededSeverities() {
        mockUser();
        when(budgetService.getProgress(1, 6, 2026)).thenReturn(List.of(
                new BudgetProgressRecord(1, 10, "Groceries", 6, 2026, BigDecimal.valueOf(100),
                        BigDecimal.valueOf(80), BigDecimal.valueOf(20), BigDecimal.valueOf(80),
                        BudgetStatus.NEAR_LIMIT),
                new BudgetProgressRecord(2, 20, "Transport", 6, 2026, BigDecimal.valueOf(100),
                        BigDecimal.valueOf(120), BigDecimal.valueOf(-20), BigDecimal.valueOf(120),
                        BudgetStatus.OVER_BUDGET)
        ));
        when(cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(1)).thenReturn(List.of());
        when(accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(1)).thenReturn(List.of());

        List<AlertRecord> alerts = service().getAlerts(6, 2026, null, null);

        assertEquals(AlertSeverity.WARNING, findAlert(alerts, AlertType.BUDGET_NEAR_LIMIT).severity());
        assertEquals(AlertSeverity.CRITICAL, findAlert(alerts, AlertType.BUDGET_EXCEEDED).severity());
    }

    @Test
    void dueSoonAlertIncludesOnlyUnpaidStatementInsideWindow() {
        mockUser();
        Card dueInsideWindow = card(10, "Nubank", BigDecimal.valueOf(1000), 20);
        Card paidCard = card(11, "Paid Card", BigDecimal.valueOf(1000), 20);
        when(cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(1))
                .thenReturn(List.of(dueInsideWindow, paidCard));
        when(installmentRepository.sumCardStatement(1, 10, 6, 2026)).thenReturn(BigDecimal.valueOf(300));
        when(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(1, 10, 6, 2026)).thenReturn(BigDecimal.valueOf(100));
        when(installmentRepository.sumCardStatement(1, 11, 6, 2026)).thenReturn(BigDecimal.valueOf(300));
        when(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(1, 11, 6, 2026)).thenReturn(BigDecimal.valueOf(300));
        when(budgetService.getProgress(1, 6, 2026)).thenReturn(List.of());
        when(accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(1)).thenReturn(List.of());

        List<AlertRecord> alerts = service().getAlerts(6, 2026,
                LocalDate.of(2026, 6, 18), LocalDate.of(2026, 6, 25));

        AlertRecord alert = findAlert(alerts, AlertType.CARD_BILL_DUE_SOON);
        assertEquals(10, alert.resourceId());
        assertEquals(BigDecimal.valueOf(200), alert.amount());
    }

    @Test
    void overdueCardBillAlertAppearsForPastDueRemainingStatement() {
        mockUser();
        Card card = card(10, "Nubank", BigDecimal.valueOf(1000), 10);
        when(cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(1)).thenReturn(List.of(card));
        when(installmentRepository.sumCardStatement(1, 10, 6, 2026)).thenReturn(BigDecimal.valueOf(300));
        when(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(1, 10, 6, 2026)).thenReturn(BigDecimal.valueOf(50));
        when(budgetService.getProgress(1, 6, 2026)).thenReturn(List.of());
        when(accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(1)).thenReturn(List.of());

        List<AlertRecord> alerts = service().getAlerts(6, 2026,
                LocalDate.of(2026, 6, 18), LocalDate.of(2026, 6, 25));

        AlertRecord alert = findAlert(alerts, AlertType.CARD_BILL_OVERDUE);
        assertEquals(AlertSeverity.CRITICAL, alert.severity());
        assertEquals(LocalDate.of(2026, 6, 10), alert.dueDate());
    }

    @Test
    void lowBalanceAlertAppearsOnlyForActiveAccountsBelowThreshold() {
        mockUser();
        Account lowBalance = new Account(10, "Checking", AccountType.CHECKING,
                BigDecimal.valueOf(99), "BRL", true, 1);
        Account enoughBalance = new Account(11, "Savings", AccountType.SAVINGS,
                BigDecimal.valueOf(100), "BRL", true, 1);
        when(budgetService.getProgress(1, 6, 2026)).thenReturn(List.of());
        when(cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(1)).thenReturn(List.of());
        when(accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(1)).thenReturn(List.of(lowBalance, enoughBalance));

        List<AlertRecord> alerts = service().getAlerts(6, 2026, null, null);

        AlertRecord alert = findAlert(alerts, AlertType.LOW_ACCOUNT_BALANCE);
        assertEquals(10, alert.resourceId());
        assertEquals(BigDecimal.valueOf(99), alert.amount());
    }

    @Test
    void defaultsUseAuthenticatedUserAndCurrentMonthWindow() {
        mockUser();
        Card card = card(10, "Nubank", BigDecimal.valueOf(1000), 20);
        when(cardRepository.findByUserIdAndActiveTrueOrderByBankNameAscNameAsc(1)).thenReturn(List.of(card));
        when(installmentRepository.sumCardStatement(1, 10, 6, 2026)).thenReturn(BigDecimal.valueOf(200));
        when(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(1, 10, 6, 2026)).thenReturn(BigDecimal.ZERO);
        when(budgetService.getProgress(1, 6, 2026)).thenReturn(List.of());
        when(accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(1)).thenReturn(List.of());

        List<AlertRecord> alerts = service().getAlerts(null, null, null, null);

        AlertRecord alert = findAlert(alerts, AlertType.CARD_BILL_DUE_SOON);
        assertEquals(6, alert.month());
        assertEquals(2026, alert.year());
    }

    private AlertService service() {
        return new AlertService(authenticatedUserService, budgetService, cardRepository, accountRepository,
                installmentRepository, cardPaymentRepository, CLOCK);
    }

    private void mockUser() {
        when(authenticatedUserService.getAuthenticatedUser())
                .thenReturn(new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com"));
    }

    private Card card(Integer id, String name, BigDecimal creditLimit, Integer dueDay) {
        return new Card(id, "Bank", name, "Visa", "1234", creditLimit, 25, dueDay, true, 1);
    }

    private boolean hasAlert(List<AlertRecord> alerts, AlertType type) {
        return alerts.stream().anyMatch(alert -> alert.type() == type);
    }

    private AlertRecord findAlert(List<AlertRecord> alerts, AlertType type) {
        return alerts.stream()
                .filter(alert -> alert.type() == type)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Alert not found: " + type));
    }
}
