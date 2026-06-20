package com.example.finanx.Services;

import com.example.finanx.DTO.CategorySummaryRecord;
import com.example.finanx.DTO.MonthlyTrendRecord;
import com.example.finanx.Entities.Category;
import com.example.finanx.Entities.CategoryType;
import com.example.finanx.Entities.TransactionType;
import com.example.finanx.Entities.User;
import com.example.finanx.repositories.CategoryRepository;
import com.example.finanx.repositories.ExpenseInstallmentRepository;
import com.example.finanx.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

@Service
public class SpendingInsightService {
    private final AuthenticatedUserService authenticatedUserService;
    private final TransactionRepository transactionRepository;
    private final ExpenseInstallmentRepository installmentRepository;
    private final CategoryRepository categoryRepository;

    public SpendingInsightService(AuthenticatedUserService authenticatedUserService,
                                  TransactionRepository transactionRepository,
                                  ExpenseInstallmentRepository installmentRepository,
                                  CategoryRepository categoryRepository) {
        this.authenticatedUserService = authenticatedUserService;
        this.transactionRepository = transactionRepository;
        this.installmentRepository = installmentRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CategorySummaryRecord> getCategorySummary(Integer month, Integer year) {
        validateMonthYear(month, year);
        User user = authenticatedUserService.getAuthenticatedUser();
        return toCategorySummary(user.getId(), getSpendingByCategory(user.getId(), month, year));
    }

    public List<MonthlyTrendRecord> getTrends(Integer fromMonth, Integer fromYear, Integer toMonth, Integer toYear) {
        validateMonthYear(fromMonth, fromYear);
        validateMonthYear(toMonth, toYear);
        User user = authenticatedUserService.getAuthenticatedUser();
        YearMonth from = YearMonth.of(fromYear, fromMonth);
        YearMonth to = YearMonth.of(toYear, toMonth);
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start month must be before or equal to end month");
        }

        List<MonthlyTrendRecord> trends = new ArrayList<>();
        YearMonth cursor = from;
        while (!cursor.isAfter(to)) {
            Integer month = cursor.getMonthValue();
            Integer year = cursor.getYear();
            BigDecimal incomeTotal = sumByType(user.getId(), TransactionType.INCOME, month, year);
            BigDecimal accountExpenseTotal = sumByType(user.getId(), TransactionType.EXPENSE, month, year);
            BigDecimal cardStatementExpenseTotal = zeroIfNull(
                    installmentRepository.sumCardStatementByUserIdAndDueMonth(user.getId(), month, year));
            BigDecimal netTotal = incomeTotal.subtract(accountExpenseTotal).subtract(cardStatementExpenseTotal);
            trends.add(new MonthlyTrendRecord(month, year, incomeTotal, accountExpenseTotal,
                    cardStatementExpenseTotal, netTotal));
            cursor = cursor.plusMonths(1);
        }
        return trends;
    }

    public Map<Integer, BigDecimal> getSpendingByCategory(Integer userId, Integer month, Integer year) {
        Map<Integer, BigDecimal> totals = new HashMap<>();
        mergeGroupedTotals(totals, transactionRepository.sumExpenseTransactionsByCategory(userId, month, year,
                TransactionType.EXPENSE));
        mergeGroupedTotals(totals, installmentRepository.sumCardStatementInstallmentsByCategory(userId, month, year));
        return totals;
    }

    private List<CategorySummaryRecord> toCategorySummary(Integer userId, Map<Integer, BigDecimal> totals) {
        return totals.entrySet().stream()
                .sorted(Comparator.comparing(entry -> categorySortName(userId, entry.getKey())))
                .map(entry -> toCategorySummaryRecord(userId, entry.getKey(), entry.getValue()))
                .toList();
    }

    private CategorySummaryRecord toCategorySummaryRecord(Integer userId, Integer categoryId, BigDecimal amount) {
        if (categoryId == null) {
            return new CategorySummaryRecord(null, "Uncategorized", CategoryType.EXPENSE, amount);
        }
        Optional<Category> category = categoryRepository.findByIdAndUserId(categoryId, userId);
        return category.map(value -> new CategorySummaryRecord(value.getId(), value.getName(), value.getType(), amount))
                .orElseGet(() -> new CategorySummaryRecord(categoryId, "Unknown category", CategoryType.EXPENSE, amount));
    }

    private String categorySortName(Integer userId, Integer categoryId) {
        if (categoryId == null) {
            return "zzzz_uncategorized";
        }
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .map(Category::getName)
                .orElse("zzzz_unknown");
    }

    private void mergeGroupedTotals(Map<Integer, BigDecimal> totals, List<Object[]> groupedTotals) {
        for (Object[] row : groupedTotals) {
            Integer categoryId = (Integer) row[0];
            BigDecimal amount = zeroIfNull((BigDecimal) row[1]);
            totals.merge(categoryId, amount, BigDecimal::add);
        }
    }

    private BigDecimal sumByType(Integer userId, TransactionType type, Integer month, Integer year) {
        return zeroIfNull(transactionRepository.sumByUserIdTypeAndMonth(userId, type, month, year));
    }

    private void validateMonthYear(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year == null || year < 1900) {
            throw new IllegalArgumentException("Year must be valid");
        }
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
