package com.example.prospera.Services;

import com.example.prospera.DTO.UserPreferenceRecord;
import com.example.prospera.Entities.*;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.AccountRepository;
import com.example.prospera.repositories.CardRepository;
import com.example.prospera.repositories.CategoryRepository;
import com.example.prospera.repositories.UserPreferenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserPreferenceService {
    private static final MovementKind DEFAULT_MOVEMENT_KIND = MovementKind.CARD_EXPENSE;
    private static final int DEFAULT_INSTALLMENT_COUNT = 1;

    private final UserPreferenceRepository preferenceRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final CategoryRepository categoryRepository;

    public UserPreferenceService(UserPreferenceRepository preferenceRepository,
                                 AuthenticatedUserService authenticatedUserService,
                                 AccountRepository accountRepository,
                                 CardRepository cardRepository,
                                 CategoryRepository categoryRepository) {
        this.preferenceRepository = preferenceRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.categoryRepository = categoryRepository;
    }

    public UserPreferenceRecord findAuthenticatedUserPreferences() {
        User user = authenticatedUserService.getAuthenticatedUser();
        return new UserPreferenceRecord(sanitizedPreference(user.getId()));
    }

    public UserPreferenceRecord updateAuthenticatedUserPreferences(UserPreferenceRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Preferences body is required");
        }
        User user = authenticatedUserService.getAuthenticatedUser();
        validate(record, user.getId());
        UserPreference preference = preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> defaultPreference(user.getId()));
        preference.setDefaultMovementKind(record.defaultMovementKind() == null
                ? DEFAULT_MOVEMENT_KIND
                : record.defaultMovementKind());
        preference.setDefaultAccountId(record.defaultAccountId());
        preference.setDefaultTargetAccountId(record.defaultTargetAccountId());
        preference.setDefaultCardId(record.defaultCardId());
        preference.setDefaultExpenseCategoryId(record.defaultExpenseCategoryId());
        preference.setDefaultIncomeCategoryId(record.defaultIncomeCategoryId());
        preference.setDefaultInstallmentCount(record.defaultInstallmentCount() == null
                ? DEFAULT_INSTALLMENT_COUNT
                : record.defaultInstallmentCount());
        return new UserPreferenceRecord(preferenceRepository.save(preference));
    }

    public void clearAccountPreference(Integer userId, Integer accountId) {
        preferenceRepository.findByUserId(userId).ifPresent(preference -> {
            boolean changed = false;
            if (accountId.equals(preference.getDefaultAccountId())) {
                preference.setDefaultAccountId(null);
                changed = true;
            }
            if (accountId.equals(preference.getDefaultTargetAccountId())) {
                preference.setDefaultTargetAccountId(null);
                changed = true;
            }
            if (changed) {
                preferenceRepository.save(preference);
            }
        });
    }

    public void clearCardPreference(Integer userId, Integer cardId) {
        preferenceRepository.findByUserId(userId).ifPresent(preference -> {
            if (cardId.equals(preference.getDefaultCardId())) {
                preference.setDefaultCardId(null);
                preferenceRepository.save(preference);
            }
        });
    }

    public void clearCategoryPreference(Integer userId, Integer categoryId) {
        preferenceRepository.findByUserId(userId).ifPresent(preference -> {
            boolean changed = false;
            if (categoryId.equals(preference.getDefaultExpenseCategoryId())) {
                preference.setDefaultExpenseCategoryId(null);
                changed = true;
            }
            if (categoryId.equals(preference.getDefaultIncomeCategoryId())) {
                preference.setDefaultIncomeCategoryId(null);
                changed = true;
            }
            if (changed) {
                preferenceRepository.save(preference);
            }
        });
    }

    private UserPreference sanitizedPreference(Integer userId) {
        UserPreference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> defaultPreference(userId));
        boolean changed = applySafeDefaults(preference);
        changed = clearInactiveOrMissingReferences(preference, userId) || changed;
        if (preference.getId() != null && changed) {
            return preferenceRepository.save(preference);
        }
        return preference;
    }

    private UserPreference defaultPreference(Integer userId) {
        return new UserPreference(null, userId, DEFAULT_MOVEMENT_KIND, null, null, null, null, null,
                DEFAULT_INSTALLMENT_COUNT);
    }

    private boolean applySafeDefaults(UserPreference preference) {
        boolean changed = false;
        if (preference.getDefaultMovementKind() == null) {
            preference.setDefaultMovementKind(DEFAULT_MOVEMENT_KIND);
            changed = true;
        }
        if (preference.getDefaultInstallmentCount() == null || preference.getDefaultInstallmentCount() < 1) {
            preference.setDefaultInstallmentCount(DEFAULT_INSTALLMENT_COUNT);
            changed = true;
        }
        return changed;
    }

    private boolean clearInactiveOrMissingReferences(UserPreference preference, Integer userId) {
        boolean changed = false;
        if (preference.getDefaultAccountId() != null && !activeAccountExists(userId, preference.getDefaultAccountId())) {
            preference.setDefaultAccountId(null);
            changed = true;
        }
        if (preference.getDefaultTargetAccountId() != null
                && !activeAccountExists(userId, preference.getDefaultTargetAccountId())) {
            preference.setDefaultTargetAccountId(null);
            changed = true;
        }
        if (preference.getDefaultCardId() != null && !activeCardExists(userId, preference.getDefaultCardId())) {
            preference.setDefaultCardId(null);
            changed = true;
        }
        if (preference.getDefaultExpenseCategoryId() != null
                && !activeCategoryExists(userId, preference.getDefaultExpenseCategoryId(), CategoryType.EXPENSE)) {
            preference.setDefaultExpenseCategoryId(null);
            changed = true;
        }
        if (preference.getDefaultIncomeCategoryId() != null
                && !activeCategoryExists(userId, preference.getDefaultIncomeCategoryId(), CategoryType.INCOME)) {
            preference.setDefaultIncomeCategoryId(null);
            changed = true;
        }
        return changed;
    }

    private void validate(UserPreferenceRecord record, Integer userId) {
        if (record.defaultInstallmentCount() != null && record.defaultInstallmentCount() < 1) {
            throw new IllegalArgumentException("Default installment count must be greater than zero");
        }
        validateAccount(userId, record.defaultAccountId(), "Default account");
        validateAccount(userId, record.defaultTargetAccountId(), "Default target account");
        validateCard(userId, record.defaultCardId());
        validateCategory(userId, record.defaultExpenseCategoryId(), CategoryType.EXPENSE, "Default expense category");
        validateCategory(userId, record.defaultIncomeCategoryId(), CategoryType.INCOME, "Default income category");
    }

    private void validateAccount(Integer userId, Integer accountId, String label) {
        if (accountId == null) {
            return;
        }
        Account account = accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new ObjectNotFoundException(label + " not found with id: " + accountId));
        if (!Boolean.TRUE.equals(account.getActive())) {
            throw new IllegalArgumentException(label + " is inactive");
        }
    }

    private void validateCard(Integer userId, Integer cardId) {
        if (cardId == null) {
            return;
        }
        Card card = cardRepository.findByIdAndUserId(cardId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Default card not found with id: " + cardId));
        if (!Boolean.TRUE.equals(card.getActive())) {
            throw new IllegalArgumentException("Default card is inactive");
        }
    }

    private void validateCategory(Integer userId, Integer categoryId, CategoryType type, String label) {
        if (categoryId == null) {
            return;
        }
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ObjectNotFoundException(label + " not found with id: " + categoryId));
        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new IllegalArgumentException(label + " is inactive");
        }
        if (category.getType() != type) {
            throw new IllegalArgumentException(label + " type must be " + type);
        }
    }

    private boolean activeAccountExists(Integer userId, Integer accountId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .filter(account -> Boolean.TRUE.equals(account.getActive()))
                .isPresent();
    }

    private boolean activeCardExists(Integer userId, Integer cardId) {
        return cardRepository.findByIdAndUserId(cardId, userId)
                .filter(card -> Boolean.TRUE.equals(card.getActive()))
                .isPresent();
    }

    private boolean activeCategoryExists(Integer userId, Integer categoryId, CategoryType type) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .filter(category -> Boolean.TRUE.equals(category.getActive()))
                .filter(category -> category.getType() == type)
                .isPresent();
    }
}
