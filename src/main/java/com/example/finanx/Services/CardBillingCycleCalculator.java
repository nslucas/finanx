package com.example.finanx.Services;

import com.example.finanx.Entities.Card;

import java.time.LocalDate;
import java.time.YearMonth;

public final class CardBillingCycleCalculator {
    private CardBillingCycleCalculator() {
    }

    public static LocalDate firstDueDate(Card card, LocalDate purchaseDate) {
        YearMonth purchaseMonth = YearMonth.from(purchaseDate);
        LocalDate closingDate = atConfiguredDay(purchaseMonth, card.getClosingDay());
        YearMonth statementMonth = purchaseDate.isAfter(closingDate) ? purchaseMonth.plusMonths(1) : purchaseMonth;
        YearMonth dueMonth = card.getDueDay() <= card.getClosingDay() ? statementMonth.plusMonths(1) : statementMonth;
        return atConfiguredDay(dueMonth, card.getDueDay());
    }

    public static LocalDate closingDateForDueMonth(Card card, Integer month, Integer year) {
        YearMonth dueMonth = YearMonth.of(year, month);
        YearMonth statementMonth = card.getDueDay() <= card.getClosingDay() ? dueMonth.minusMonths(1) : dueMonth;
        return atConfiguredDay(statementMonth, card.getClosingDay());
    }

    public static LocalDate atConfiguredDay(YearMonth month, Integer day) {
        int clampedDay = Math.min(day, month.lengthOfMonth());
        return month.atDay(clampedDay);
    }
}
