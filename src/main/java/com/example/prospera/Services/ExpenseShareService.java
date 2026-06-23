package com.example.prospera.Services;

import com.example.prospera.DTO.ExpenseShareRequest;
import com.example.prospera.Entities.Expense;
import com.example.prospera.Entities.ExpenseShare;
import com.example.prospera.Entities.ExpenseShareStatus;
import com.example.prospera.Entities.User;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.ExpenseShareRepository;
import com.example.prospera.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Transactional
public class ExpenseShareService {
    private static final String TIME_ZONE = "America/Sao_Paulo";

    private final ExpenseShareRepository shareRepository;
    private final UserRepository userRepository;
    private final ConnectionService connectionService;

    public ExpenseShareService(ExpenseShareRepository shareRepository, UserRepository userRepository,
                               ConnectionService connectionService) {
        this.shareRepository = shareRepository;
        this.userRepository = userRepository;
        this.connectionService = connectionService;
    }

    public void createForExpense(User creator, Expense expense, ExpenseShareRequest request) {
        if (request == null) {
            return;
        }
        ExpenseShare share = buildShare(null, creator, expense, request);
        shareRepository.save(share);
    }

    public void updateForExpense(User creator, Expense expense, ExpenseShareRequest request) {
        ExpenseShare existing = shareRepository.findByExpenseId(expense.getId()).orElse(null);
        if (existing == null) {
            createForExpense(creator, expense, request);
            return;
        }
        if (existing.getStatus() == ExpenseShareStatus.SETTLED) {
            throw new IllegalArgumentException("Settled shared expenses cannot be updated");
        }
        if (request == null) {
            shareRepository.delete(existing);
            return;
        }
        ExpenseShare updated = buildShare(existing, creator, expense, request);
        shareRepository.save(updated);
    }

    public void deleteForExpense(Integer expenseId) {
        ExpenseShare existing = shareRepository.findByExpenseId(expenseId).orElse(null);
        if (existing == null) {
            return;
        }
        if (existing.getStatus() == ExpenseShareStatus.SETTLED) {
            throw new IllegalArgumentException("Settled shared expenses cannot be deleted");
        }
        shareRepository.delete(existing);
    }

    private ExpenseShare buildShare(ExpenseShare existing, User creator, Expense expense, ExpenseShareRequest request) {
        validateShareRequest(creator, expense, request);
        ExpenseShare share = existing == null ? new ExpenseShare() : existing;
        share.setExpenseId(expense.getId());
        share.setCreatorUserId(creator.getId());
        share.setParticipantUserId(request.participantUserId());
        share.setCreatorAmount(request.creatorAmount());
        share.setParticipantAmount(request.participantAmount());
        if (share.getCreatedAt() == null) {
            share.setCreatedAt(now());
        }
        if (share.getStatus() == null) {
            share.setStatus(ExpenseShareStatus.OPEN);
        }
        return share;
    }

    private void validateShareRequest(User creator, Expense expense, ExpenseShareRequest request) {
        if (request.participantUserId() == null) {
            throw new IllegalArgumentException("Participant user id is required");
        }
        if (creator.getId().equals(request.participantUserId())) {
            throw new IllegalArgumentException("Participant must be different from creator");
        }
        if (!userRepository.existsById(request.participantUserId())) {
            throw new ObjectNotFoundException("Participant user not found with id: " + request.participantUserId());
        }
        if (!connectionService.areConnected(creator.getId(), request.participantUserId())) {
            throw new IllegalArgumentException("Users must have an accepted connection to share expenses");
        }
        if (request.creatorAmount() == null || request.creatorAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Creator amount must be zero or greater");
        }
        if (request.participantAmount() == null || request.participantAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Participant amount must be greater than zero");
        }
        BigDecimal total = request.creatorAmount().add(request.participantAmount());
        if (total.compareTo(expense.getAmount()) != 0) {
            throw new IllegalArgumentException("Shared amounts must add up to the expense amount");
        }
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of(TIME_ZONE));
    }
}
