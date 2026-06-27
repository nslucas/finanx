package com.example.prospera.Services;

import com.example.prospera.DTO.ConnectionRequestRecord;
import com.example.prospera.DTO.UserConnectionRecord;
import com.example.prospera.Entities.ConnectionStatus;
import com.example.prospera.Entities.User;
import com.example.prospera.Entities.UserConnection;
import com.example.prospera.repositories.UserConnectionRepository;
import com.example.prospera.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceTest {
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private UserConnectionRepository connectionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;

    @Test
    void createRequestCreatesPendingConnectionFromCode() {
        User maria = user(1, "Maria");
        User lucas = user(2, "Lucas");
        lucas.setConnectionCode("ABC12345");
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(userRepository.findByConnectionCode("ABC12345")).thenReturn(lucas);
        when(connectionRepository.findBetweenUsersWithStatuses(1, 2,
                List.of(ConnectionStatus.PENDING, ConnectionStatus.ACCEPTED))).thenReturn(List.of());
        when(userRepository.findById(1)).thenReturn(Optional.of(maria));
        when(userRepository.findById(2)).thenReturn(Optional.of(lucas));
        when(connectionRepository.save(any(UserConnection.class))).thenAnswer(invocation -> {
            UserConnection connection = invocation.getArgument(0);
            connection.setId(10);
            return connection;
        });

        UserConnectionRecord record = service().createRequest(new ConnectionRequestRecord("abc12345"));

        assertEquals(10, record.id());
        assertEquals(ConnectionStatus.PENDING, record.status());
        assertEquals("Maria", record.requesterName());
        assertEquals("Lucas", record.targetName());
        verify(notificationService).create(2,
                com.example.prospera.Entities.NotificationType.CONNECTION_REQUEST_RECEIVED,
                com.example.prospera.Entities.NotificationCategory.CONNECTION_REQUEST,
                "Nova solicitação de conexão",
                "Maria quer se conectar com você.",
                "/connections",
                "CONNECTION_REQUEST",
                10,
                "CONNECTION_REQUEST_RECEIVED:2:10");
    }

    @Test
    void createRequestRejectsSelfConnection() {
        User maria = user(1, "Maria");
        maria.setConnectionCode("ABC12345");
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(userRepository.findByConnectionCode("ABC12345")).thenReturn(maria);

        assertThrows(IllegalArgumentException.class,
                () -> service().createRequest(new ConnectionRequestRecord("ABC12345")));
        verify(connectionRepository, never()).save(any());
    }

    @Test
    void createRequestRejectsDuplicatePendingOrAcceptedConnection() {
        User maria = user(1, "Maria");
        User lucas = user(2, "Lucas");
        UserConnection existing = new UserConnection(5, 2, 1, ConnectionStatus.PENDING, null, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(userRepository.findByConnectionCode("ABC12345")).thenReturn(lucas);
        when(connectionRepository.findBetweenUsersWithStatuses(1, 2,
                List.of(ConnectionStatus.PENDING, ConnectionStatus.ACCEPTED))).thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class,
                () -> service().createRequest(new ConnectionRequestRecord("ABC12345")));
        verify(connectionRepository, never()).save(any());
    }

    @Test
    void acceptAllowsOnlyPendingTargetUser() {
        User lucas = user(2, "Lucas");
        UserConnection pending = new UserConnection(10, 1, 2, ConnectionStatus.PENDING, null, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(lucas);
        when(connectionRepository.findById(10)).thenReturn(Optional.of(pending));
        when(connectionRepository.save(any(UserConnection.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserConnectionRecord record = service().accept(10);

        assertEquals(ConnectionStatus.ACCEPTED, record.status());
    }

    @Test
    void declineRejectsRequesterAnsweringOwnRequest() {
        User maria = user(1, "Maria");
        UserConnection pending = new UserConnection(10, 1, 2, ConnectionStatus.PENDING, null, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(maria);
        when(connectionRepository.findById(10)).thenReturn(Optional.of(pending));

        assertThrows(IllegalArgumentException.class, () -> service().decline(10));
        verify(connectionRepository, never()).save(any());
    }

    @Test
    void findPendingRequestsUsesAuthenticatedTargetUser() {
        User lucas = user(2, "Lucas");
        User maria = user(1, "Maria");
        UserConnection pending = new UserConnection(10, 1, 2, ConnectionStatus.PENDING, null, null);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(lucas);
        when(connectionRepository.findByTargetUserIdAndStatusOrderByRequestedAtAsc(2, ConnectionStatus.PENDING))
                .thenReturn(List.of(pending));
        when(userRepository.findById(1)).thenReturn(Optional.of(maria));
        when(userRepository.findById(2)).thenReturn(Optional.of(lucas));

        assertEquals(1, service().findPendingRequests().size());
    }

    private ConnectionService service() {
        return new ConnectionService(authenticatedUserService, connectionRepository, userRepository, userService,
                notificationService);
    }

    private User user(Integer id, String name) {
        return new User(id, name, "", BigDecimal.valueOf(1000), name.toLowerCase() + "@test.com");
    }
}
