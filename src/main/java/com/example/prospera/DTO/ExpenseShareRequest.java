package com.example.prospera.DTO;

import java.math.BigDecimal;

public record ExpenseShareRequest(Integer participantUserId, BigDecimal creatorAmount, BigDecimal participantAmount) {
}
