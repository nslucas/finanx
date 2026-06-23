package com.example.prospera.DTO;

import com.example.prospera.Entities.ConnectionStatus;
import com.example.prospera.Entities.User;
import com.example.prospera.Entities.UserConnection;

import java.time.LocalDateTime;

public record UserConnectionRecord(Integer id, Integer requesterUserId, String requesterName,
                                   Integer targetUserId, String targetName, ConnectionStatus status,
                                   LocalDateTime requestedAt, LocalDateTime respondedAt) {
    public UserConnectionRecord(UserConnection connection, User requester, User target) {
        this(connection.getId(), connection.getRequesterUserId(), fullName(requester),
                connection.getTargetUserId(), fullName(target), connection.getStatus(),
                connection.getRequestedAt(), connection.getRespondedAt());
    }

    private static String fullName(User user) {
        if (user == null) {
            return null;
        }
        String lastName = user.getLastName() == null ? "" : " " + user.getLastName();
        return (user.getName() + lastName).trim();
    }
}
