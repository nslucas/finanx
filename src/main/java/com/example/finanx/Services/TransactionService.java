package com.example.finanx.Services;

import com.example.finanx.DTO.TransactionRecord;
import com.example.finanx.DTO.TransferRecord;
import com.example.finanx.Entities.Account;
import com.example.finanx.Entities.Transaction;
import com.example.finanx.Entities.TransactionType;
import com.example.finanx.Entities.User;
import com.example.finanx.Exceptions.ObjectNotFoundException;
import com.example.finanx.Repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    private static final String TIME_ZONE = "America/Sao_Paulo";

    private final TransactionRepository transactionRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AuthenticatedUserService authenticatedUserService,
                              AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.accountService = accountService;
    }

    public List<Transaction> findAuthenticatedUserTransactions(Integer month, Integer year, Integer accountId,
                                                               TransactionType type) {
        User user = authenticatedUserService.getAuthenticatedUser();
        validateMonthYear(month, year);
        if (accountId != null) {
            accountService.findUserAccount(user.getId(), accountId);
        }
        return transactionRepository.findByFilters(user.getId(), month, year, accountId, type);
    }

    public Transaction findAuthenticatedUserTransaction(Integer id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        return findUserTransaction(user.getId(), id);
    }

    public Transaction create(TransactionRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Transaction body is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        if (record.type() == TransactionType.TRANSFER_IN || record.type() == TransactionType.TRANSFER_OUT ||
                record.type() == TransactionType.CARD_PAYMENT) {
            throw new IllegalArgumentException("Use the dedicated endpoint for transfers and card payments");
        }
        return post(user.getId(), record.accountId(), record.type(), record.amount(), record.occurredAt(),
                record.description(), null, true);
    }

    public Transaction createCardPaymentTransaction(Integer userId, Integer accountId, BigDecimal amount,
                                                    LocalDateTime occurredAt, String description) {
        return post(userId, accountId, TransactionType.CARD_PAYMENT, amount, occurredAt, description, null, true);
    }

    public void createTransfer(Integer sourceAccountId, TransferRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Transfer body is required");
        }
        if (record.targetAccountId() == null) {
            throw new IllegalArgumentException("Target account id is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        validatePositiveAmount(record.amount());
        Account source = accountService.findUserAccount(user.getId(), sourceAccountId);
        Account target = accountService.findUserAccount(user.getId(), record.targetAccountId());
        accountService.ensureActive(source);
        accountService.ensureActive(target);
        if (source.getId().equals(target.getId())) {
            throw new IllegalArgumentException("Transfer target must be different from source account");
        }

        LocalDateTime occurredAt = defaultOccurredAt(record.occurredAt());
        accountService.applyDelta(user.getId(), source.getId(), record.amount().negate());
        accountService.applyDelta(user.getId(), target.getId(), record.amount());

        Transaction out = saveTransaction(user.getId(), source.getId(), TransactionType.TRANSFER_OUT,
                record.amount(), occurredAt, record.description(), null);
        Transaction in = saveTransaction(user.getId(), target.getId(), TransactionType.TRANSFER_IN,
                record.amount(), occurredAt, record.description(), out.getId());
        out.setRelatedTransactionId(in.getId());
        transactionRepository.save(out);
    }

    public void delete(Integer id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        Transaction transaction = findUserTransaction(user.getId(), id);
        if (transaction.getType() == TransactionType.CARD_PAYMENT) {
            throw new IllegalArgumentException("Card payment transactions cannot be deleted directly");
        }
        reverseBalanceEffect(transaction);
        if (transaction.getRelatedTransactionId() != null) {
            transactionRepository.findByIdAndUserId(transaction.getRelatedTransactionId(), user.getId())
                    .ifPresent(related -> {
                        reverseBalanceEffect(related);
                        transactionRepository.delete(related);
                    });
        }
        transactionRepository.delete(transaction);
    }

    public BigDecimal sumByType(Integer userId, TransactionType type, Integer month, Integer year) {
        BigDecimal total = transactionRepository.sumByUserIdTypeAndMonth(userId, type, month, year);
        return total == null ? BigDecimal.ZERO : total;
    }

    private Transaction post(Integer userId, Integer accountId, TransactionType type, BigDecimal amount,
                             LocalDateTime occurredAt, String description, Integer relatedTransactionId,
                             boolean updateBalance) {
        validateTransaction(type, amount);
        Account account = accountService.findUserAccount(userId, accountId);
        accountService.ensureActive(account);
        if (updateBalance) {
            accountService.applyDelta(userId, accountId, balanceDelta(type, amount));
        }
        return saveTransaction(userId, accountId, type, amount, defaultOccurredAt(occurredAt), description, relatedTransactionId);
    }

    private Transaction saveTransaction(Integer userId, Integer accountId, TransactionType type, BigDecimal amount,
                                        LocalDateTime occurredAt, String description, Integer relatedTransactionId) {
        Transaction transaction = new Transaction(null, type, amount, occurredAt, description, accountId,
                relatedTransactionId, userId);
        return transactionRepository.save(transaction);
    }

    private Transaction findUserTransaction(Integer userId, Integer id) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Transaction not found with id: " + id));
    }

    private void reverseBalanceEffect(Transaction transaction) {
        accountService.applyDelta(transaction.getUserId(), transaction.getAccountId(),
                balanceDelta(transaction.getType(), transaction.getAmount()).negate());
    }

    private BigDecimal balanceDelta(TransactionType type, BigDecimal amount) {
        return switch (type) {
            case INCOME, TRANSFER_IN, ADJUSTMENT -> amount;
            case EXPENSE, TRANSFER_OUT, CARD_PAYMENT -> amount.negate();
        };
    }

    private void validateTransaction(TransactionType type, BigDecimal amount) {
        if (type == null) {
            throw new IllegalArgumentException("Transaction type is required");
        }
        if (type == TransactionType.ADJUSTMENT) {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Adjustment amount cannot be zero");
            }
            return;
        }
        validatePositiveAmount(amount);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private void validateMonthYear(Integer month, Integer year) {
        if ((month == null && year != null) || (month != null && year == null)) {
            throw new IllegalArgumentException("Month and year filters must be used together");
        }
        if (month != null && (month < 1 || month > 12)) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year != null && year < 1900) {
            throw new IllegalArgumentException("Year must be valid");
        }
    }

    private LocalDateTime defaultOccurredAt(LocalDateTime occurredAt) {
        return occurredAt == null ? LocalDateTime.now(ZoneId.of(TIME_ZONE)) : occurredAt;
    }
}
