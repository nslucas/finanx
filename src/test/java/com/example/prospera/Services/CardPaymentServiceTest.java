package com.example.prospera.Services;

import com.example.prospera.DTO.CardPaymentRecord;
import com.example.prospera.Entities.*;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.CardPaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardPaymentServiceTest {
    @Mock
    private CardPaymentRepository cardPaymentRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private CardService cardService;
    @Mock
    private AccountService accountService;
    @Mock
    private TransactionService transactionService;

    @Test
    void updatePaymentPreservesTransactionAndUpdatesPaymentFields() {
        User user = user();
        Card card = card();
        Account account = account(10);
        CardPayment payment = payment(50, 2, 10, BigDecimal.valueOf(100), 90);
        CardPaymentRecord record = new CardPaymentRecord(null, null, 10, 7, 2026,
                BigDecimal.valueOf(300), LocalDate.of(2026, 7, 8), "Updated payment", null);
        Transaction transaction = new Transaction(90, TransactionType.CARD_PAYMENT, BigDecimal.valueOf(300),
                LocalDateTime.of(2026, 7, 8, 12, 0), "Updated payment", 10, null, 1);

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.findUserCard(1, 2)).thenReturn(card);
        when(cardPaymentRepository.findByIdAndUserIdAndCardId(50, 1, 2)).thenReturn(Optional.of(payment));
        when(accountService.findUserAccount(1, 10)).thenReturn(account);
        when(transactionService.updateCardPaymentTransaction(1, 90, 10, BigDecimal.valueOf(300),
                LocalDateTime.of(LocalDate.of(2026, 7, 8), LocalTime.NOON), "Updated payment"))
                .thenReturn(transaction);
        when(cardPaymentRepository.save(any(CardPayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardPaymentService service = service();
        CardPayment updated = service.update(2, 50, record);

        assertEquals(50, updated.getId());
        assertEquals(2, updated.getCardId());
        assertEquals(10, updated.getAccountId());
        assertEquals(7, updated.getMonth());
        assertEquals(2026, updated.getYear());
        assertEquals(BigDecimal.valueOf(300), updated.getAmount());
        assertEquals(LocalDate.of(2026, 7, 8), updated.getPaymentDate());
        assertEquals("Updated payment", updated.getDescription());
        assertEquals(90, updated.getTransactionId());
        verify(accountService).ensureActive(account);
    }

    @Test
    void updatePaymentAllowsChangingSourceAccount() {
        User user = user();
        CardPayment payment = payment(50, 2, 10, BigDecimal.valueOf(100), 90);
        Account newAccount = account(20);
        CardPaymentRecord record = new CardPaymentRecord(null, null, 20, 6, 2026,
                BigDecimal.valueOf(250), LocalDate.of(2026, 6, 9), "Changed account", null);

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.findUserCard(1, 2)).thenReturn(card());
        when(cardPaymentRepository.findByIdAndUserIdAndCardId(50, 1, 2)).thenReturn(Optional.of(payment));
        when(accountService.findUserAccount(1, 20)).thenReturn(newAccount);
        when(transactionService.updateCardPaymentTransaction(any(), any(), any(), any(), any(), any()))
                .thenReturn(new Transaction(90, TransactionType.CARD_PAYMENT, BigDecimal.valueOf(250),
                        LocalDateTime.of(2026, 6, 9, 12, 0), "Changed account", 20, null, 1));
        when(cardPaymentRepository.save(any(CardPayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CardPayment updated = service().update(2, 50, record);

        assertEquals(20, updated.getAccountId());
        verify(transactionService).updateCardPaymentTransaction(1, 90, 20, BigDecimal.valueOf(250),
                LocalDateTime.of(LocalDate.of(2026, 6, 9), LocalTime.NOON), "Changed account");
    }

    @Test
    void updatePaymentRejectsPaymentFromAnotherCardOrUser() {
        CardPaymentRecord record = new CardPaymentRecord(null, null, 10, 6, 2026,
                BigDecimal.valueOf(300), LocalDate.of(2026, 6, 8), "Payment", null);

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user());
        when(cardService.findUserCard(1, 2)).thenReturn(card());
        when(cardPaymentRepository.findByIdAndUserIdAndCardId(50, 1, 2)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service().update(2, 50, record));
        verifyNoInteractions(transactionService);
    }

    @Test
    void deletePaymentReversesBalanceDeletesPaymentThenDeletesTransaction() {
        CardPayment payment = payment(50, 2, 10, BigDecimal.valueOf(100), 90);
        Transaction transaction = new Transaction(90, TransactionType.CARD_PAYMENT, BigDecimal.valueOf(100),
                LocalDateTime.of(2026, 6, 8, 12, 0), "Payment", 10, null, 1);

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user());
        when(cardService.findUserCard(1, 2)).thenReturn(card());
        when(cardPaymentRepository.findByIdAndUserIdAndCardId(50, 1, 2)).thenReturn(Optional.of(payment));
        when(transactionService.reverseCardPaymentTransaction(1, 90)).thenReturn(transaction);

        service().delete(2, 50);

        InOrder inOrder = inOrder(transactionService, cardPaymentRepository);
        inOrder.verify(transactionService).reverseCardPaymentTransaction(1, 90);
        inOrder.verify(cardPaymentRepository).delete(payment);
        inOrder.verify(cardPaymentRepository).flush();
        inOrder.verify(transactionService).deleteReversedCardPaymentTransaction(1, 90);
    }

    @Test
    void deletePaymentRejectsPaymentFromAnotherCardOrUser() {
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user());
        when(cardService.findUserCard(1, 2)).thenReturn(card());
        when(cardPaymentRepository.findByIdAndUserIdAndCardId(50, 1, 2)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service().delete(2, 50));
        verifyNoInteractions(transactionService);
        verify(cardPaymentRepository, never()).delete(any());
    }

    private CardPaymentService service() {
        return new CardPaymentService(cardPaymentRepository, authenticatedUserService, cardService, accountService,
                transactionService);
    }

    private User user() {
        return new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
    }

    private Card card() {
        return new Card(2, "Nubank", "Purple", "Mastercard", "1234",
                BigDecimal.valueOf(5000), 25, 10, true, 1);
    }

    private Account account(Integer id) {
        return new Account(id, "Checking", AccountType.CHECKING, BigDecimal.valueOf(1000), "BRL", true, 1);
    }

    private CardPayment payment(Integer id, Integer cardId, Integer accountId, BigDecimal amount, Integer transactionId) {
        return new CardPayment(id, cardId, accountId, 1, 6, 2026, amount,
                LocalDate.of(2026, 6, 8), "Payment", transactionId);
    }
}
