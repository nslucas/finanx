package com.example.prospera.Services;

import com.example.prospera.Entities.CardStatementStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardStatementServiceTest {

    @Test
    void unpaidStatementIsOpen() {
        assertEquals(CardStatementStatus.OPEN,
                CardStatementService.resolveStatus(BigDecimal.valueOf(100), BigDecimal.ZERO));
    }

    @Test
    void lowerPaymentIsPartial() {
        assertEquals(CardStatementStatus.PARTIALLY_PAID,
                CardStatementService.resolveStatus(BigDecimal.valueOf(100), BigDecimal.valueOf(40)));
    }

    @Test
    void exactPaymentIsPaid() {
        assertEquals(CardStatementStatus.PAID,
                CardStatementService.resolveStatus(BigDecimal.valueOf(100), BigDecimal.valueOf(100)));
    }

    @Test
    void higherPaymentIsOverpaid() {
        assertEquals(CardStatementStatus.OVERPAID,
                CardStatementService.resolveStatus(BigDecimal.valueOf(100), BigDecimal.valueOf(120)));
    }
}
