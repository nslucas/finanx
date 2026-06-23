package com.example.prospera.repositories;

import com.example.prospera.Entities.ExpenseInstallment;
import com.example.prospera.Entities.ExpenseInstallmentId;
import com.example.prospera.DTO.ExpenseInstallmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseInstallmentRepository extends JpaRepository <ExpenseInstallment, ExpenseInstallmentId> {
    List<ExpenseInstallment> findById_ExpenseId(Integer expenseId);

    @Modifying
    void deleteById_ExpenseId(Integer expenseId);

    @Query("SELECT SUM(i.installmentAmount) FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.userId = :userId " +
            "AND MONTH(i.dueDate) = :month AND YEAR(i.dueDate) = :year")
    BigDecimal sumByUserIdAndDueMonth(@Param("userId") Integer userId, @Param("month") Integer month,
                                      @Param("year") Integer year);

    @Query("SELECT DISTINCT e FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.userId = :userId " +
            "AND MONTH(i.dueDate) = :month AND YEAR(i.dueDate) = :year")
    List<com.example.prospera.Entities.Expense> findExpensesByUserIdAndDueMonth(@Param("userId") Integer userId,
                                                                               @Param("month") Integer month,
                                                                               @Param("year") Integer year);

    @Query("SELECT i FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.cardId = :cardId AND e.userId = :userId " +
            "AND MONTH(i.dueDate) = :month AND YEAR(i.dueDate) = :year " +
            "ORDER BY i.dueDate ASC, i.id.installmentNumber ASC")
    List<ExpenseInstallment> findCardStatementInstallments(@Param("userId") Integer userId,
                                                           @Param("cardId") Integer cardId,
                                                           @Param("month") Integer month,
                                                           @Param("year") Integer year);

    @Query("SELECT new com.example.prospera.DTO.ExpenseInstallmentRecord(" +
            "i.id.expenseId, i.id.installmentNumber, e.name, i.installmentAmount, i.dueDate, e.cardId) " +
            "FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.cardId = :cardId AND e.userId = :userId " +
            "AND i.dueDate >= :from AND i.dueDate < :to " +
            "ORDER BY i.dueDate ASC, i.id.installmentNumber ASC")
    List<ExpenseInstallmentRecord> findCardStatementInstallmentRecords(@Param("userId") Integer userId,
                                                                       @Param("cardId") Integer cardId,
                                                                       @Param("from") LocalDate from,
                                                                       @Param("to") LocalDate to);

    @Query("SELECT SUM(i.installmentAmount) FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.cardId = :cardId AND e.userId = :userId " +
            "AND MONTH(i.dueDate) = :month AND YEAR(i.dueDate) = :year")
    BigDecimal sumCardStatement(@Param("userId") Integer userId, @Param("cardId") Integer cardId,
                                @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT SUM(i.installmentAmount) FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.cardId = :cardId AND e.userId = :userId " +
            "AND i.dueDate >= :from AND i.dueDate < :to")
    BigDecimal sumCardStatementInDueDateRange(@Param("userId") Integer userId,
                                              @Param("cardId") Integer cardId,
                                              @Param("from") LocalDate from,
                                              @Param("to") LocalDate to);

    @Query("SELECT SUM(i.installmentAmount) FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.userId = :userId AND e.cardId IS NOT NULL " +
            "AND MONTH(i.dueDate) = :month AND YEAR(i.dueDate) = :year")
    BigDecimal sumCardStatementByUserIdAndDueMonth(@Param("userId") Integer userId,
                                                   @Param("month") Integer month,
                                                   @Param("year") Integer year);

    @Query("SELECT e.categoryId, SUM(i.installmentAmount) FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.userId = :userId AND e.cardId IS NOT NULL " +
            "AND i.dueDate >= :from AND i.dueDate < :to " +
            "GROUP BY e.categoryId")
    List<Object[]> sumCardStatementInstallmentsByCategoryInDueDateRange(@Param("userId") Integer userId,
                                                                        @Param("from") LocalDate from,
                                                                        @Param("to") LocalDate to);

    @Query("SELECT e.cardId, SUM(i.installmentAmount) FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.userId = :userId AND e.cardId IS NOT NULL " +
            "AND i.dueDate >= :from AND i.dueDate < :to " +
            "GROUP BY e.cardId")
    List<Object[]> sumCardStatementsByCardInDueDateRange(@Param("userId") Integer userId,
                                                         @Param("from") LocalDate from,
                                                         @Param("to") LocalDate to);

    @Query("SELECT e.cardId, YEAR(i.dueDate), MONTH(i.dueDate), SUM(i.installmentAmount) " +
            "FROM ExpenseInstallment i, Expense e " +
            "WHERE i.id.expenseId = e.id AND e.userId = :userId AND e.cardId IS NOT NULL " +
            "AND i.dueDate >= :from AND i.dueDate < :to " +
            "GROUP BY e.cardId, YEAR(i.dueDate), MONTH(i.dueDate)")
    List<Object[]> sumCardStatementsByCardAndDueMonthInRange(@Param("userId") Integer userId,
                                                             @Param("from") LocalDate from,
                                                             @Param("to") LocalDate to);
}
