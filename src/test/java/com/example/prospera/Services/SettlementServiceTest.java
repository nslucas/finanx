package com.example.prospera.Services;

import com.example.prospera.DTO.SettlementItemRecord;
import com.example.prospera.DTO.SettlementSummaryRecord;
import com.example.prospera.Entities.Expense;
import com.example.prospera.Entities.ExpenseShare;
import com.example.prospera.Entities.ExpenseShareStatus;
import com.example.prospera.Entities.SettlementDirection;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.ExpenseRepository;
import com.example.prospera.repositories.ExpenseShareRepository;
import com.example.prospera.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private ExpenseShareRepository shareRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void getSummaryNetsOpenSharesAndIgnoresSettledItems() {
        User maria = user(1, "Maria");
        User lucas = user(2, "Lucas");
        ExpenseShare lucasOwesMaria = share(10, 100, 1, 2, 40, ExpenseShareStatus.OPEN);
        ExpenseShare mariaOwesLucas = share(11, 101, 2, 1, 15, ExpenseShareStatus.OPEN);
        ExpenseShare settled = share(12, 102, 1, 2, 99, ExpenseShareStatus.SETTLED);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(shareRepository.findByCreatorUserIdOrParticipantUserId(1, 1))
                .thenReturn(List.of(lucasOwesMaria, mariaOwesLucas, settled));
        when(userRepository.findById(2)).thenReturn(Optional.of(lucas));

        SettlementService service = new SettlementService(authenticatedUserService, shareRepository, expenseRepository,
                userRepository);
        List<SettlementSummaryRecord> summary = service.getSummary();

        assertEquals(1, summary.size());
        assertEquals(BigDecimal.valueOf(25), summary.get(0).amount());
        assertEquals(SettlementDirection.OWES_YOU, summary.get(0).direction());
    }

    @Test
    void getItemsReturnsDirectionForParticipantDebt() {
        User maria = user(1, "Maria");
        User lucas = user(2, "Lucas");
        ExpenseShare share = share(10, 100, 2, 1, 15, ExpenseShareStatus.OPEN);
        Expense expense = new Expense(100, "Jantar", BigDecimal.valueOf(55), 1, LocalDateTime.now(), "", 2, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(shareRepository.findSettlementItems(1, 2)).thenReturn(List.of(share));
        when(expenseRepository.findById(100)).thenReturn(Optional.of(expense));
        when(userRepository.findById(1)).thenReturn(Optional.of(maria));
        when(userRepository.findById(2)).thenReturn(Optional.of(lucas));

        SettlementService service = new SettlementService(authenticatedUserService, shareRepository, expenseRepository,
                userRepository);
        List<SettlementItemRecord> items = service.getItems(2);

        assertEquals(1, items.size());
        assertEquals(SettlementDirection.YOU_OWE, items.get(0).direction());
    }

    @Test
    void settleMarksOpenItemAsSettled() {
        User maria = user(1, "Maria");
        User lucas = user(2, "Lucas");
        ExpenseShare share = share(10, 100, 1, 2, 40, ExpenseShareStatus.OPEN);
        Expense expense = new Expense(100, "Mercado", BigDecimal.valueOf(55), 1, LocalDateTime.now(), "", 1, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(shareRepository.findByIdForUser(10, 1)).thenReturn(Optional.of(share));
        when(shareRepository.save(any(ExpenseShare.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(expenseRepository.findById(100)).thenReturn(Optional.of(expense));
        when(userRepository.findById(1)).thenReturn(Optional.of(maria));
        when(userRepository.findById(2)).thenReturn(Optional.of(lucas));

        SettlementService service = new SettlementService(authenticatedUserService, shareRepository, expenseRepository,
                userRepository);
        SettlementItemRecord item = service.settle(10);

        assertEquals(ExpenseShareStatus.SETTLED, item.status());
    }

    @Test
    void settleRejectsAlreadySettledItem() {
        User maria = user(1, "Maria");
        ExpenseShare share = share(10, 100, 1, 2, 40, ExpenseShareStatus.SETTLED);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(shareRepository.findByIdForUser(10, 1)).thenReturn(Optional.of(share));

        SettlementService service = new SettlementService(authenticatedUserService, shareRepository, expenseRepository,
                userRepository);

        assertThrows(IllegalArgumentException.class, () -> service.settle(10));
    }

    private User user(Integer id, String name) {
        return new User(id, name, "", BigDecimal.valueOf(1000), name.toLowerCase() + "@test.com");
    }

    private ExpenseShare share(Integer id, Integer expenseId, Integer creatorUserId, Integer participantUserId,
                               int participantAmount, ExpenseShareStatus status) {
        return new ExpenseShare(id, expenseId, creatorUserId, participantUserId,
                BigDecimal.valueOf(55 - participantAmount), BigDecimal.valueOf(participantAmount), status,
                LocalDateTime.now(), status == ExpenseShareStatus.SETTLED ? LocalDateTime.now() : null);
    }
}
