package com.example.prospera.Services;

import com.example.prospera.DTO.ExpenseShareRequest;
import com.example.prospera.Entities.Expense;
import com.example.prospera.Entities.ExpenseShare;
import com.example.prospera.Entities.ExpenseShareStatus;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.ExpenseShareRepository;
import com.example.prospera.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseShareServiceTest {
    @Mock
    private ExpenseShareRepository shareRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConnectionService connectionService;

    @Test
    void createForExpensePersistsOpenShareForAcceptedConnection() {
        User maria = user(1, "Maria");
        Expense expense = expense();
        when(userRepository.existsById(2)).thenReturn(true);
        when(connectionService.areConnected(1, 2)).thenReturn(true);

        ExpenseShareService service = new ExpenseShareService(shareRepository, userRepository, connectionService);
        service.createForExpense(maria, expense,
                new ExpenseShareRequest(2, BigDecimal.valueOf(15), BigDecimal.valueOf(40)));

        verify(shareRepository).save(any(ExpenseShare.class));
    }

    @Test
    void createForExpenseRejectsInvalidAmountSum() {
        User maria = user(1, "Maria");
        Expense expense = expense();
        when(userRepository.existsById(2)).thenReturn(true);
        when(connectionService.areConnected(1, 2)).thenReturn(true);

        ExpenseShareService service = new ExpenseShareService(shareRepository, userRepository, connectionService);

        assertThrows(IllegalArgumentException.class, () -> service.createForExpense(maria, expense,
                new ExpenseShareRequest(2, BigDecimal.valueOf(10), BigDecimal.valueOf(40))));
        verify(shareRepository, never()).save(any());
    }

    @Test
    void createForExpenseRequiresAcceptedConnection() {
        User maria = user(1, "Maria");
        Expense expense = expense();
        when(userRepository.existsById(2)).thenReturn(true);
        when(connectionService.areConnected(1, 2)).thenReturn(false);

        ExpenseShareService service = new ExpenseShareService(shareRepository, userRepository, connectionService);

        assertThrows(IllegalArgumentException.class, () -> service.createForExpense(maria, expense,
                new ExpenseShareRequest(2, BigDecimal.valueOf(15), BigDecimal.valueOf(40))));
        verify(shareRepository, never()).save(any());
    }

    @Test
    void updateForExpenseRejectsSettledShare() {
        User maria = user(1, "Maria");
        Expense expense = expense();
        ExpenseShare settled = new ExpenseShare(7, 10, 1, 2, BigDecimal.valueOf(15), BigDecimal.valueOf(40),
                ExpenseShareStatus.SETTLED, LocalDateTime.now(), LocalDateTime.now());
        when(shareRepository.findByExpenseId(10)).thenReturn(Optional.of(settled));

        ExpenseShareService service = new ExpenseShareService(shareRepository, userRepository, connectionService);

        assertThrows(IllegalArgumentException.class, () -> service.updateForExpense(maria, expense,
                new ExpenseShareRequest(2, BigDecimal.valueOf(20), BigDecimal.valueOf(35))));
        verify(shareRepository, never()).save(any());
    }

    @Test
    void deleteForExpenseRejectsSettledShare() {
        ExpenseShare settled = new ExpenseShare(7, 10, 1, 2, BigDecimal.valueOf(15), BigDecimal.valueOf(40),
                ExpenseShareStatus.SETTLED, LocalDateTime.now(), LocalDateTime.now());
        when(shareRepository.findByExpenseId(10)).thenReturn(Optional.of(settled));

        ExpenseShareService service = new ExpenseShareService(shareRepository, userRepository, connectionService);

        assertThrows(IllegalArgumentException.class, () -> service.deleteForExpense(10));
        verify(shareRepository, never()).delete(any());
    }

    private User user(Integer id, String name) {
        return new User(id, name, "", BigDecimal.valueOf(1000), name.toLowerCase() + "@test.com");
    }

    private Expense expense() {
        return new Expense(10, "Mercado", BigDecimal.valueOf(55), 1, LocalDateTime.now(), "Compra", 1, null, 3);
    }
}
