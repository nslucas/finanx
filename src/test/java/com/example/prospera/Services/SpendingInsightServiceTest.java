package com.example.prospera.Services;

import com.example.prospera.DTO.CategorySummaryRecord;
import com.example.prospera.Entities.Category;
import com.example.prospera.Entities.CategoryType;
import com.example.prospera.Entities.TransactionType;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.CategoryRepository;
import com.example.prospera.repositories.ExpenseInstallmentRepository;
import com.example.prospera.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpendingInsightServiceTest {
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ExpenseInstallmentRepository installmentRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void categorySummaryCombinesAccountExpensesAndCardInstallments() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(transactionRepository.sumExpenseTransactionsByCategory(1, 6, 2026, TransactionType.EXPENSE))
                .thenReturn(List.<Object[]>of(new Object[]{50, BigDecimal.valueOf(70)}));
        when(installmentRepository.sumCardStatementInstallmentsByCategory(1, 6, 2026))
                .thenReturn(List.<Object[]>of(new Object[]{50, BigDecimal.valueOf(30)}, new Object[]{null, BigDecimal.valueOf(10)}));
        when(categoryRepository.findByIdAndUserId(50, 1))
                .thenReturn(Optional.of(new Category(50, "Groceries", CategoryType.EXPENSE, true, 1)));

        SpendingInsightService service = new SpendingInsightService(authenticatedUserService, transactionRepository,
                installmentRepository, categoryRepository);

        List<CategorySummaryRecord> summary = service.getCategorySummary(6, 2026);

        assertEquals(2, summary.size());
        assertEquals(50, summary.get(0).categoryId());
        assertEquals(BigDecimal.valueOf(100), summary.get(0).amount());
        assertEquals("Uncategorized", summary.get(1).categoryName());
        assertEquals(BigDecimal.valueOf(10), summary.get(1).amount());
    }
}
