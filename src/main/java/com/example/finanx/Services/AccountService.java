package com.example.finanx.Services;

import com.example.finanx.DTO.AccountRecord;
import com.example.finanx.Entities.Account;
import com.example.finanx.Entities.Transaction;
import com.example.finanx.Entities.TransactionType;
import com.example.finanx.Entities.User;
import com.example.finanx.Exceptions.ObjectNotFoundException;
import com.example.finanx.Repositories.AccountRepository;
import com.example.finanx.Repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class AccountService {
    private static final String TIME_ZONE = "America/Sao_Paulo";

    private final AccountRepository accountRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, AuthenticatedUserService authenticatedUserService,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.transactionRepository = transactionRepository;
    }

    public List<Account> findAllActiveForAuthenticatedUser() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return accountRepository.findByUserIdAndActiveTrueOrderByNameAsc(user.getId());
    }

    public Account findAuthenticatedUserAccount(Integer id) {
        User user = authenticatedUserService.getAuthenticatedUser();
        return findUserAccount(user.getId(), id);
    }

    public Account findUserAccount(Integer userId, Integer id) {
        return accountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Account not found with id: " + id));
    }

    public Account create(AccountRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Account body is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        validateCreate(record);
        Account account = fromRecord(record);
        account.setId(null);
        account.setUserId(user.getId());
        account.setActive(true);
        Account saved = accountRepository.save(account);
        if (saved.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            transactionRepository.save(new Transaction(null, TransactionType.ADJUSTMENT, saved.getBalance(),
                    LocalDateTime.now(ZoneId.of(TIME_ZONE)), "Opening account balance",
                    saved.getId(), null, user.getId()));
        }
        return saved;
    }

    public Account update(Integer id, AccountRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Account body is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        validateMetadata(record);
        Account account = findUserAccount(user.getId(), id);
        account.setName(record.name());
        account.setType(record.type());
        account.setCurrency(normalizeCurrency(record.currency()));
        return accountRepository.save(account);
    }

    public void deactivate(Integer id) {
        Account account = findAuthenticatedUserAccount(id);
        account.setActive(false);
        accountRepository.save(account);
    }

    public Account applyDelta(Integer userId, Integer accountId, BigDecimal delta) {
        Account account = findUserAccount(userId, accountId);
        ensureActive(account);
        BigDecimal newBalance = account.getBalance().add(delta);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient account balance");
        }
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }

    public BigDecimal getTotalActiveBalance(Integer userId) {
        BigDecimal total = accountRepository.sumActiveBalancesByUserId(userId);
        return total == null ? BigDecimal.ZERO : total;
    }

    public void ensureActive(Account account) {
        if (!Boolean.TRUE.equals(account.getActive())) {
            throw new IllegalArgumentException("Account is inactive");
        }
    }

    private Account fromRecord(AccountRecord record) {
        return new Account(record.id(), record.name(), record.type(), record.balance(),
                normalizeCurrency(record.currency()), record.active(), null);
    }

    private void validateCreate(AccountRecord record) {
        validateMetadata(record);
        if (record.balance() == null || record.balance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Account balance must be zero or greater");
        }
    }

    private void validateMetadata(AccountRecord record) {
        if (record.name() == null || record.name().isBlank()) {
            throw new IllegalArgumentException("Account name is required");
        }
        if (record.type() == null) {
            throw new IllegalArgumentException("Account type is required");
        }
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "BRL";
        }
        return currency.trim().toUpperCase();
    }
}
