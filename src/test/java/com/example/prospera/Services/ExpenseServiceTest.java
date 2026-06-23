package com.example.prospera.Services;

import com.example.prospera.DTO.ExpenseRecord;
import com.example.prospera.DTO.ExpenseShareRequest;
import com.example.prospera.Entities.Expense;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.ExpenseInstallmentRepository;
import com.example.prospera.repositories.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private UserService userService;
    @Mock
    private ExpenseInstallmentService installmentService;
    @Mock
    private ExpenseInstallmentRepository installmentRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private CardService cardService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private ExpenseShareService expenseShareService;

    @Test
    void createExpenseWithSharePersistsExpenseInstallmentsAndShare() {
        User maria = new User(1, "Maria", "", BigDecimal.valueOf(1000), "maria@test.com");
        ExpenseShareRequest share = new ExpenseShareRequest(2, BigDecimal.valueOf(15), BigDecimal.valueOf(40));
        ExpenseRecord record = new ExpenseRecord(null, "Mercado", BigDecimal.valueOf(55), 1,
                LocalDateTime.of(2026, 6, 23, 10, 0), "Compra dividida", null, null, null, share);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense expense = invocation.getArgument(0);
            expense.setId(10);
            return expense;
        });

        ExpenseService service = new ExpenseService(expenseRepository, userService, installmentService,
                installmentRepository, authenticatedUserService, cardService, categoryService, expenseShareService);
        Expense expense = service.createExpense(record);

        assertEquals(10, expense.getId());
        verify(installmentService).generateInstallments(expense, null);
        verify(expenseShareService).createForExpense(maria, expense, share);
    }
}
