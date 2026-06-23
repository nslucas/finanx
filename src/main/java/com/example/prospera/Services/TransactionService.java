package com.example.prospera.Services;

import com.example.prospera.DTO.TransactionRecord;
import com.example.prospera.DTO.TransferRecord;
import com.example.prospera.Entities.Account;
import com.example.prospera.Entities.CategoryType;
import com.example.prospera.Entities.Transaction;
import com.example.prospera.Entities.TransactionType;
import com.example.prospera.Entities.User;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    private static final String TIME_ZONE = "America/Sao_Paulo";

    private final TransactionRepository transactionRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository, AuthenticatedUserService authenticatedUserService,
                              AccountService accountService, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    public List<Transaction> findAuthenticatedUserTransactions(Integer month, Integer year, Integer accountId,
                                                               TransactionType type) {
        User user = authenticatedUserService.getAuthenticatedUser();
        validateMonthYear(month, year);
        if (accountId != null) {
            accountService.findUserAccount(user.getId(), accountId);
        }
        YearMonth filterMonth = month == null ? null : YearMonth.of(year, month);
        LocalDateTime from = filterMonth == null ? null : filterMonth.atDay(1).atStartOfDay();
        LocalDateTime to = filterMonth == null ? null : filterMonth.plusMonths(1).atDay(1).atStartOfDay();
        return transactionRepository.findByFilters(user.getId(), from, to, accountId, type);
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
                record.description(), null, record.categoryId(), true);
    }

    public Transaction createCardPaymentTransaction(Integer userId, Integer accountId, BigDecimal amount,
                                                    LocalDateTime occurredAt, String description) {
        return post(userId, accountId, TransactionType.CARD_PAYMENT, amount, occurredAt, description, null, null, true);
    }

    public Transaction updateCardPaymentTransaction(Integer userId, Integer transactionId, Integer accountId,
                                                    BigDecimal amount, LocalDateTime occurredAt, String description) {
        validateTransaction(TransactionType.CARD_PAYMENT, amount);
        Account account = accountService.findUserAccount(userId, accountId);
        accountService.ensureActive(account);
        Transaction transaction = findUserTransaction(userId, transactionId);
        requireCardPaymentTransaction(transaction);

        reverseBalanceEffect(transaction);
        accountService.applyDelta(userId, accountId, balanceDelta(TransactionType.CARD_PAYMENT, amount));

        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setOccurredAt(defaultOccurredAt(occurredAt));
        transaction.setDescription(description);
        return transactionRepository.save(transaction);
    }

    public Transaction reverseCardPaymentTransaction(Integer userId, Integer transactionId) {
        Transaction transaction = findUserTransaction(userId, transactionId);
        requireCardPaymentTransaction(transaction);
        reverseBalanceEffect(transaction);
        return transaction;
    }

    public void deleteReversedCardPaymentTransaction(Integer userId, Integer transactionId) {
        Transaction transaction = findUserTransaction(userId, transactionId);
        requireCardPaymentTransaction(transaction);
        transactionRepository.delete(transaction);
    }

    public Transaction createRecurringTransaction(Integer userId, Integer accountId, TransactionType type,
                                                  BigDecimal amount, LocalDateTime occurredAt, String description,
                                                  Integer categoryId) {
        if (type != TransactionType.INCOME && type != TransactionType.EXPENSE) {
            throw new IllegalArgumentException("Recurring account transactions must be income or expense");
        }
        return post(userId, accountId, type, amount, occurredAt, description, null, categoryId, true);
    }

    public void deleteRecurringTransaction(Integer userId, Integer transactionId) {
        Transaction transaction = findUserTransaction(userId, transactionId);
        if (transaction.getType() != TransactionType.INCOME && transaction.getType() != TransactionType.EXPENSE) {
            throw new IllegalArgumentException("Recurring account transactions must be income or expense");
        }
        reverseBalanceEffect(transaction);
        transactionRepository.delete(transaction);
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
                record.amount(), occurredAt, record.description(), null, null);
        Transaction in = saveTransaction(user.getId(), target.getId(), TransactionType.TRANSFER_IN,
                record.amount(), occurredAt, record.description(), out.getId(), null);
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
        YearMonth filterMonth = YearMonth.of(year, month);
        BigDecimal total = transactionRepository.sumByUserIdTypeAndDateRange(userId, type,
                filterMonth.atDay(1).atStartOfDay(), filterMonth.plusMonths(1).atDay(1).atStartOfDay());
        return total == null ? BigDecimal.ZERO : total;
    }

    private Transaction post(Integer userId, Integer accountId, TransactionType type, BigDecimal amount,
                             LocalDateTime occurredAt, String description, Integer relatedTransactionId,
                             Integer categoryId, boolean updateBalance) {
        validateTransaction(type, amount);
        validateCategory(userId, type, categoryId);
        Account account = accountService.findUserAccount(userId, accountId);
        accountService.ensureActive(account);
        if (updateBalance) {
            accountService.applyDelta(userId, accountId, balanceDelta(type, amount));
        }
        return saveTransaction(userId, accountId, type, amount, defaultOccurredAt(occurredAt), description,
                relatedTransactionId, categoryId);
    }

    private Transaction saveTransaction(Integer userId, Integer accountId, TransactionType type, BigDecimal amount,
                                        LocalDateTime occurredAt, String description, Integer relatedTransactionId,
                                        Integer categoryId) {
        Transaction transaction = new Transaction(null, type, amount, occurredAt, description, accountId,
                relatedTransactionId, userId, categoryId);
        return transactionRepository.save(transaction);
    }

    private Transaction findUserTransaction(Integer userId, Integer id) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Transaction not found with id: " + id));
    }

    private void requireCardPaymentTransaction(Transaction transaction) {
        if (transaction.getType() != TransactionType.CARD_PAYMENT) {
            throw new IllegalArgumentException("Transaction is not a card payment");
        }
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

    private void validateCategory(Integer userId, TransactionType type, Integer categoryId) {
        if (categoryId == null) {
            return;
        }
        switch (type) {
            case INCOME -> categoryService.requireActiveCategory(userId, categoryId, CategoryType.INCOME);
            case EXPENSE -> categoryService.requireActiveCategory(userId, categoryId, CategoryType.EXPENSE);
            case TRANSFER_IN, TRANSFER_OUT, CARD_PAYMENT, ADJUSTMENT ->
                    throw new IllegalArgumentException("Category is not allowed for this transaction type");
        }
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
