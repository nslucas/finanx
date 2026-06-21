package com.example.prospera.Services;

import com.example.prospera.Entities.Card;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardBillingCycleCalculatorTest {

    @Test
    void purchaseBeforeClosingDayEntersCurrentStatement() {
        Card card = cardWithCycle(25, 10);

        LocalDate dueDate = CardBillingCycleCalculator.firstDueDate(card, LocalDate.of(2026, 6, 20));

        assertEquals(LocalDate.of(2026, 7, 10), dueDate);
    }

    @Test
    void purchaseOnClosingDayEntersCurrentStatement() {
        Card card = cardWithCycle(25, 10);

        LocalDate dueDate = CardBillingCycleCalculator.firstDueDate(card, LocalDate.of(2026, 6, 25));

        assertEquals(LocalDate.of(2026, 7, 10), dueDate);
    }

    @Test
    void purchaseAfterClosingDayEntersNextStatement() {
        Card card = cardWithCycle(25, 10);

        LocalDate dueDate = CardBillingCycleCalculator.firstDueDate(card, LocalDate.of(2026, 6, 26));

        assertEquals(LocalDate.of(2026, 8, 10), dueDate);
    }

    @Test
    void dueDayAfterClosingDayFallsInSameStatementMonth() {
        Card card = cardWithCycle(10, 25);

        LocalDate dueDate = CardBillingCycleCalculator.firstDueDate(card, LocalDate.of(2026, 6, 5));

        assertEquals(LocalDate.of(2026, 6, 25), dueDate);
    }

    @Test
    void dueDayIsClampedForShortMonths() {
        Card card = cardWithCycle(31, 31);

        LocalDate dueDate = CardBillingCycleCalculator.firstDueDate(card, LocalDate.of(2026, 1, 15));

        assertEquals(LocalDate.of(2026, 2, 28), dueDate);
    }

    private Card cardWithCycle(Integer closingDay, Integer dueDay) {
        return new Card(1, "Bank", "Card", "Visa", "1234", BigDecimal.valueOf(1000), closingDay, dueDay, true, 1);
    }
}
