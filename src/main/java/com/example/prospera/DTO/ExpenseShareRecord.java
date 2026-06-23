package com.example.prospera.DTO;

import com.example.prospera.Entities.ExpenseShare;
import com.example.prospera.Entities.ExpenseShareStatus;
import com.example.prospera.Entities.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseShareRecord(Integer id, Integer expenseId, Integer creatorUserId, Integer participantUserId,
                                 String participantName, BigDecimal creatorAmount, BigDecimal participantAmount,
                                 ExpenseShareStatus status, LocalDateTime createdAt, LocalDateTime settledAt) {
    public ExpenseShareRecord(ExpenseShare share, User participant) {
        this(share.getId(), share.getExpenseId(), share.getCreatorUserId(), share.getParticipantUserId(),
                fullName(participant), share.getCreatorAmount(), share.getParticipantAmount(), share.getStatus(),
                share.getCreatedAt(), share.getSettledAt());
    }

    private static String fullName(User user) {
        if (user == null) {
            return null;
        }
        String lastName = user.getLastName() == null ? "" : " " + user.getLastName();
        return (user.getName() + lastName).trim();
    }
}
