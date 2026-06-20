package com.example.finanx.Services;

import com.example.finanx.DTO.AccountRecord;
import com.example.finanx.Entities.*;
import com.example.finanx.repositories.AccountRepository;
import com.example.finanx.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void createAccountWithOpeningBalanceCreatesAdjustmentTransaction() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        AccountRecord record = new AccountRecord(null, "Checking", AccountType.CHECKING,
                BigDecimal.valueOf(500), "brl", true);
        Account savedAccount = new Account(10, "Checking", AccountType.CHECKING,
                BigDecimal.valueOf(500), "BRL", true, 1);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountService service = new AccountService(accountRepository, authenticatedUserService, transactionRepository);
        service.create(record);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        assertEquals(TransactionType.ADJUSTMENT, transactionCaptor.getValue().getType());
        assertEquals(BigDecimal.valueOf(500), transactionCaptor.getValue().getAmount());
        assertEquals(10, transactionCaptor.getValue().getAccountId());
    }
}
