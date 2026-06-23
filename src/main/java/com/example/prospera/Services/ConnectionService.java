package com.example.prospera.Services;

import com.example.prospera.DTO.ConnectionCodeRecord;
import com.example.prospera.DTO.ConnectionRequestRecord;
import com.example.prospera.DTO.UserConnectionRecord;
import com.example.prospera.Entities.ConnectionStatus;
import com.example.prospera.Entities.User;
import com.example.prospera.Entities.UserConnection;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.UserConnectionRepository;
import com.example.prospera.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class ConnectionService {
    private static final String TIME_ZONE = "America/Sao_Paulo";

    private final AuthenticatedUserService authenticatedUserService;
    private final UserConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public ConnectionService(AuthenticatedUserService authenticatedUserService,
                             UserConnectionRepository connectionRepository,
                             UserRepository userRepository,
                             UserService userService) {
        this.authenticatedUserService = authenticatedUserService;
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public ConnectionCodeRecord getAuthenticatedUserCode() {
        User user = authenticatedUserService.getAuthenticatedUser();
        if (user.getConnectionCode() == null || user.getConnectionCode().isBlank()) {
            user.setConnectionCode(userService.generateUniqueConnectionCode());
            userRepository.save(user);
        }
        return new ConnectionCodeRecord(user.getConnectionCode());
    }

    public UserConnectionRecord createRequest(ConnectionRequestRecord record) {
        if (record == null || record.targetCode() == null || record.targetCode().isBlank()) {
            throw new IllegalArgumentException("Target code is required");
        }
        User requester = authenticatedUserService.getAuthenticatedUser();
        User target = userRepository.findByConnectionCode(record.targetCode().trim().toUpperCase());
        if (target == null) {
            throw new ObjectNotFoundException("Connection code not found");
        }
        if (requester.getId().equals(target.getId())) {
            throw new IllegalArgumentException("Cannot connect with yourself");
        }
        if (!connectionRepository.findBetweenUsersWithStatuses(requester.getId(), target.getId(),
                List.of(ConnectionStatus.PENDING, ConnectionStatus.ACCEPTED)).isEmpty()) {
            throw new IllegalArgumentException("A pending or accepted connection already exists");
        }

        UserConnection connection = new UserConnection(null, requester.getId(), target.getId(),
                ConnectionStatus.PENDING, now(), null);
        return toRecord(connectionRepository.save(connection));
    }

    public List<UserConnectionRecord> findPendingRequests() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return connectionRepository.findByTargetUserIdAndStatusOrderByRequestedAtAsc(user.getId(),
                        ConnectionStatus.PENDING)
                .stream()
                .map(this::toRecord)
                .toList();
    }

    public List<UserConnectionRecord> findAcceptedConnections() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return connectionRepository.findForUserByStatus(user.getId(), ConnectionStatus.ACCEPTED)
                .stream()
                .map(this::toRecord)
                .toList();
    }

    public UserConnectionRecord accept(Integer id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        UserConnection connection = findPendingTargetConnection(id, user.getId());
        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection.setRespondedAt(now());
        return toRecord(connectionRepository.save(connection));
    }

    public UserConnectionRecord decline(Integer id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        UserConnection connection = findPendingTargetConnection(id, user.getId());
        connection.setStatus(ConnectionStatus.DECLINED);
        connection.setRespondedAt(now());
        return toRecord(connectionRepository.save(connection));
    }

    public boolean areConnected(Integer firstUserId, Integer secondUserId) {
        return !connectionRepository.findBetweenUsersWithStatuses(firstUserId, secondUserId,
                List.of(ConnectionStatus.ACCEPTED)).isEmpty();
    }

    private UserConnection findPendingTargetConnection(Integer id, Integer targetUserId) {
        UserConnection connection = connectionRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Connection request not found with id: " + id));
        if (!connection.getTargetUserId().equals(targetUserId) || connection.getStatus() != ConnectionStatus.PENDING) {
            throw new IllegalArgumentException("Connection request cannot be answered by this user");
        }
        return connection;
    }

    private UserConnectionRecord toRecord(UserConnection connection) {
        User requester = userRepository.findById(connection.getRequesterUserId()).orElse(null);
        User target = userRepository.findById(connection.getTargetUserId()).orElse(null);
        return new UserConnectionRecord(connection, requester, target);
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(TIME_ZONE));
    }
}
