package com.example.finanx.Services;

import com.example.finanx.DTO.BudgetProgressRecord;
import com.example.finanx.DTO.BudgetRecord;
import com.example.finanx.Entities.*;
import com.example.finanx.Repositories.BudgetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private SpendingInsightService spendingInsightService;

    @Test
    void createBudgetRequiresExpenseCategory() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        BudgetRecord record = new BudgetRecord(null, 50, 6, 2026, BigDecimal.valueOf(800), true);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(categoryService.requireActiveCategory(1, 50, CategoryType.EXPENSE))
                .thenThrow(new IllegalArgumentException("Category type must be EXPENSE"));

        BudgetService service = new BudgetService(budgetRepository, authenticatedUserService, categoryService,
                spendingInsightService);

        assertThrows(IllegalArgumentException.class, () -> service.create(record));
        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void progressReturnsNearLimitStatus() {
        Budget budget = new Budget(10, 50, 6, 2026, BigDecimal.valueOf(100), true, 1);
        when(spendingInsightService.getSpendingByCategory(1, 6, 2026))
                .thenReturn(Map.of(50, BigDecimal.valueOf(80)));
        when(budgetRepository.findByUserIdAndActiveTrueAndMonthAndYearOrderByCategoryIdAsc(1, 6, 2026))
                .thenReturn(List.of(budget));
        when(categoryService.findUserCategory(1, 50))
                .thenReturn(new Category(50, "Groceries", CategoryType.EXPENSE, true, 1));

        BudgetService service = new BudgetService(budgetRepository, authenticatedUserService, categoryService,
                spendingInsightService);

        List<BudgetProgressRecord> progress = service.getProgress(1, 6, 2026);

        assertEquals(BudgetStatus.NEAR_LIMIT, progress.get(0).status());
        assertEquals(BigDecimal.valueOf(80), progress.get(0).spentAmount());
        assertEquals(BigDecimal.valueOf(20), progress.get(0).remainingAmount());
    }

    @Test
    void duplicateActiveBudgetIsRejected() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        BudgetRecord record = new BudgetRecord(null, 50, 6, 2026, BigDecimal.valueOf(100), true);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(categoryService.requireActiveCategory(1, 50, CategoryType.EXPENSE))
                .thenReturn(new Category(50, "Groceries", CategoryType.EXPENSE, true, 1));
        when(budgetRepository.findByUserIdAndCategoryIdAndMonthAndYearAndActiveTrue(1, 50, 6, 2026))
                .thenReturn(Optional.of(new Budget(9, 50, 6, 2026, BigDecimal.valueOf(90), true, 1)));

        BudgetService service = new BudgetService(budgetRepository, authenticatedUserService, categoryService,
                spendingInsightService);

        assertThrows(IllegalArgumentException.class, () -> service.create(record));
        verify(budgetRepository, never()).save(any(Budget.class));
    }
}
