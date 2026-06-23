package com.example.prospera.Services;

import com.example.prospera.DTO.SettlementItemRecord;
import com.example.prospera.DTO.SettlementSummaryRecord;
import com.example.prospera.Entities.Expense;
import com.example.prospera.Entities.ExpenseShare;
import com.example.prospera.Entities.ExpenseShareStatus;
import com.example.prospera.Entities.SettlementDirection;
import com.example.prospera.Entities.User;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.ExpenseRepository;
import com.example.prospera.repositories.ExpenseShareRepository;
import com.example.prospera.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SettlementService {
    private static final String TIME_ZONE = "America/Sao_Paulo";

    private final AuthenticatedUserService authenticatedUserService;
    private final ExpenseShareRepository shareRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public SettlementService(AuthenticatedUserService authenticatedUserService,
                             ExpenseShareRepository shareRepository,
                             ExpenseRepository expenseRepository,
                             UserRepository userRepository) {
        this.authenticatedUserService = authenticatedUserService;
        this.shareRepository = shareRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public List<SettlementSummaryRecord> getSummary() {
        User user = authenticatedUserService.getAuthenticatedUser();
        Map<Integer, BigDecimal> balances = new HashMap<>();
        for (ExpenseShare share : shareRepository.findByCreatorUserIdOrParticipantUserId(user.getId(), user.getId())) {
            if (share.getStatus() != ExpenseShareStatus.OPEN) {
                continue;
            }
            if (share.getCreatorUserId().equals(user.getId())) {
                balances.merge(share.getParticipantUserId(), share.getParticipantAmount(), BigDecimal::add);
            } else {
                balances.merge(share.getCreatorUserId(), share.getParticipantAmount().negate(), BigDecimal::add);
            }
        }

        List<SettlementSummaryRecord> summaries = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : balances.entrySet()) {
            BigDecimal amount = entry.getValue();
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            User counterparty = findUser(entry.getKey());
            SettlementDirection direction = amount.compareTo(BigDecimal.ZERO) > 0
                    ? SettlementDirection.OWES_YOU
                    : SettlementDirection.YOU_OWE;
            summaries.add(new SettlementSummaryRecord(counterparty.getId(), fullName(counterparty), amount.abs(),
                    direction));
        }
        return summaries;
    }

    public List<SettlementItemRecord> getItems(Integer counterpartyUserId) {
        User user = authenticatedUserService.getAuthenticatedUser();
        if (counterpartyUserId != null && counterpartyUserId.equals(user.getId())) {
            throw new IllegalArgumentException("Counterparty must be different from authenticated user");
        }
        return shareRepository.findSettlementItems(user.getId(), counterpartyUserId)
                .stream()
                .filter(share -> isUserCounterpartyFilterMatch(user.getId(), counterpartyUserId, share))
                .map(share -> toItemRecord(user.getId(), share))
                .toList();
    }

    public SettlementItemRecord settle(Integer shareId) {
        User user = authenticatedUserService.getAuthenticatedUser();
        ExpenseShare share = shareRepository.findByIdForUser(shareId, user.getId())
                .orElseThrow(() -> new ObjectNotFoundException("Settlement item not found with id: " + shareId));
        if (share.getStatus() == ExpenseShareStatus.SETTLED) {
            throw new IllegalArgumentException("Settlement item is already settled");
        }
        share.setStatus(ExpenseShareStatus.SETTLED);
        share.setSettledAt(LocalDateTime.now(ZoneId.of(TIME_ZONE)));
        return toItemRecord(user.getId(), shareRepository.save(share));
    }

    private boolean isUserCounterpartyFilterMatch(Integer userId, Integer counterpartyUserId, ExpenseShare share) {
        if (counterpartyUserId == null) {
            return true;
        }
        if (share.getCreatorUserId().equals(userId)) {
            return share.getParticipantUserId().equals(counterpartyUserId);
        }
        return share.getCreatorUserId().equals(counterpartyUserId);
    }

    private SettlementItemRecord toItemRecord(Integer userId, ExpenseShare share) {
        Expense expense = expenseRepository.findById(share.getExpenseId())
                .orElseThrow(() -> new ObjectNotFoundException("Expense not found with id: " + share.getExpenseId()));
        User creator = findUser(share.getCreatorUserId());
        User participant = findUser(share.getParticipantUserId());
        SettlementDirection direction = share.getCreatorUserId().equals(userId)
                ? SettlementDirection.OWES_YOU
                : SettlementDirection.YOU_OWE;
        return new SettlementItemRecord(share.getId(), expense.getId(), expense.getName(), expense.getAmount(),
                creator.getId(), fullName(creator), participant.getId(), fullName(participant),
                share.getParticipantAmount(), direction, share.getStatus(), share.getCreatedAt(),
                share.getSettledAt());
    }

    private User findUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found with id: " + userId));
    }

    private String fullName(User user) {
        String lastName = user.getLastName() == null ? "" : " " + user.getLastName();
        return (user.getName() + lastName).trim();
    }
}
