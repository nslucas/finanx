package com.example.prospera.DTO;

import com.example.prospera.Entities.CardPayment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardPaymentRecord(Integer id, Integer cardId, Integer accountId, Integer month, Integer year,
                                BigDecimal amount, LocalDate paymentDate, String description, Integer transactionId) {
    public CardPaymentRecord(CardPayment payment) {
        this(payment.getId(), payment.getCardId(), payment.getAccountId(), payment.getMonth(), payment.getYear(),
                payment.getAmount(), payment.getPaymentDate(), payment.getDescription(), payment.getTransactionId());
    }
}
