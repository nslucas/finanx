package com.example.prospera.DTO;

import com.example.prospera.Entities.Card;

import java.math.BigDecimal;

public record CardRecord(Integer id, String bankName, String name, String network, String lastFourDigits,
                         BigDecimal creditLimit, Integer closingDay, Integer dueDay, Boolean active) {
    public CardRecord(Card card) {
        this(card.getId(), card.getBankName(), card.getName(), card.getNetwork(), card.getLastFourDigits(),
                card.getCreditLimit(), card.getClosingDay(), card.getDueDay(), card.getActive());
    }
}
