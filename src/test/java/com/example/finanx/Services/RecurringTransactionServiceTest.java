package com.example.finanx.Services;

import com.example.finanx.DTO.RecurringOccurrenceRequest;
import com.example.finanx.Entities.*;
import com.example.finanx.repositories.RecurringOccurrenceRepository;
import com.example.finanx.repositories.RecurringTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringTransactionServiceTest {
    @Mock
    private RecurringTransactionRepository recurrenceRepository;
    @Mock
    private RecurringOccurrenceRepository occurrenceRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private AccountService accountService;
    @Mock
    private CardService cardService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private ExpenseService expenseService;

    @Test
    void monthlyRecurrenceClampsShortMonths() {
        RecurringTransaction recurrence = accountRecurrence();
        recurrence.setDayOfMonth(31);

        RecurringTransactionService service = service();

        List<LocalDate> dates = service.generateOccurrenceDates(recurrence,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));

        assertEquals(List.of(
                LocalDate.of(2026, 1, 31),
                LocalDate.of(2026, 2, 28),
                LocalDate.of(2026, 3, 31)
        ), dates);
    }

    @Test
    void materializeAccountIncomeCreatesTransactionAndOccurrence() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        RecurringTransaction recurrence = accountRecurrence();
        Transaction transaction = new Transaction(20, TransactionType.INCOME, BigDecimal.valueOf(500),
                null, "Salary", 10, null, 1, 7);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(recurrenceRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.of(recurrence));
        when(accountService.findUserAccount(1, 10)).thenReturn(new Account(10, "Checking", AccountType.CHECKING,
                BigDecimal.ZERO, "BRL", true, 1));
        when(occurrenceRepository.findByRecurrenceIdAndOccurrenceDate(99, LocalDate.of(2026, 6, 5)))
                .thenReturn(Optional.empty());
        when(transactionService.createRecurringTransaction(1, 10, TransactionType.INCOME, BigDecimal.valueOf(500),
                java.time.LocalDateTime.of(2026, 6, 5, 12, 0), "Salary", 7)).thenReturn(transaction);
        when(occurrenceRepository.save(any(RecurringOccurrence.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RecurringTransactionService service = service();
        service.materialize(99, new RecurringOccurrenceRequest(LocalDate.of(2026, 6, 5)));

        ArgumentCaptor<RecurringOccurrence> captor = ArgumentCaptor.forClass(RecurringOccurrence.class);
        verify(occurrenceRepository).save(captor.capture());
        assertEquals(RecurringOccurrenceStatus.MATERIALIZED, captor.getValue().getStatus());
        assertEquals(20, captor.getValue().getTransactionId());
    }

    @Test
    void skippedOccurrenceCannotBeMaterialized() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        RecurringTransaction recurrence = accountRecurrence();
        RecurringOccurrence skipped = new RecurringOccurrence(1, 99, LocalDate.of(2026, 6, 5),
                RecurringOccurrenceStatus.SKIPPED, null, null, 1);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(recurrenceRepository.findByIdAndUserId(99, 1)).thenReturn(Optional.of(recurrence));
        when(accountService.findUserAccount(1, 10)).thenReturn(new Account(10, "Checking", AccountType.CHECKING,
                BigDecimal.ZERO, "BRL", true, 1));
        when(occurrenceRepository.findByRecurrenceIdAndOccurrenceDate(99, LocalDate.of(2026, 6, 5)))
                .thenReturn(Optional.of(skipped));

        RecurringTransactionService service = service();

        assertThrows(IllegalArgumentException.class,
                () -> service.materialize(99, new RecurringOccurrenceRequest(LocalDate.of(2026, 6, 5))));
        verifyNoInteractions(transactionService);
        verify(occurrenceRepository, never()).save(any(RecurringOccurrence.class));
    }

    private RecurringTransaction accountRecurrence() {
        return new RecurringTransaction(99, "Salary", "Salary", RecurringTargetType.ACCOUNT_TRANSACTION,
                TransactionType.INCOME, BigDecimal.valueOf(500), RecurringFrequency.MONTHLY,
                LocalDate.of(2026, 1, 5), null, 5, null, 10, null, 7,
                null, RecurringClassification.FIXED, true, 1);
    }

    private RecurringTransactionService service() {
        return new RecurringTransactionService(recurrenceRepository, occurrenceRepository, authenticatedUserService,
                accountService, cardService, categoryService, transactionService, expenseService);
    }
}
