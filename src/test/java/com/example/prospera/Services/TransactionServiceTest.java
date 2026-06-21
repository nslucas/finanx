package com.example.prospera.Services;

import com.example.prospera.DTO.TransactionRecord;
import com.example.prospera.Entities.*;
import com.example.prospera.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private AccountService accountService;
    @Mock
    private CategoryService categoryService;

    @Test
    void createIncomeAppliesPositiveBalanceDelta() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        Account account = new Account(10, "Checking", AccountType.CHECKING, BigDecimal.ZERO, "BRL", true, 1);
        TransactionRecord record = new TransactionRecord(null, TransactionType.INCOME, BigDecimal.valueOf(100),
                null, "Salary", 10, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(accountService.findUserAccount(1, 10)).thenReturn(account);

        TransactionService service = new TransactionService(transactionRepository, authenticatedUserService, accountService,
                categoryService);
        service.create(record);

        verify(accountService).applyDelta(1, 10, BigDecimal.valueOf(100));
        verifyNoInteractions(categoryService);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void directCardPaymentTransactionIsRejected() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        TransactionRecord record = new TransactionRecord(null, TransactionType.CARD_PAYMENT, BigDecimal.valueOf(100),
                null, "Payment", 10, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);

        TransactionService service = new TransactionService(transactionRepository, authenticatedUserService, accountService,
                categoryService);

        assertThrows(IllegalArgumentException.class, () -> service.create(record));
        verifyNoInteractions(accountService);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void createExpenseWithIncomeCategoryIsRejected() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        TransactionRecord record = new TransactionRecord(null, TransactionType.EXPENSE, BigDecimal.valueOf(100),
                null, "Groceries", 10, null, 99);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(categoryService.requireActiveCategory(1, 99, CategoryType.EXPENSE))
                .thenThrow(new IllegalArgumentException("Category type must be EXPENSE"));

        TransactionService service = new TransactionService(transactionRepository, authenticatedUserService, accountService,
                categoryService);

        assertThrows(IllegalArgumentException.class, () -> service.create(record));
        verifyNoInteractions(accountService);
        verifyNoInteractions(transactionRepository);
    }
}
