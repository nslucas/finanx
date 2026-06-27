package com.example.prospera.DTO;

import com.example.prospera.Entities.MovementKind;
import com.example.prospera.Entities.UserPreference;

public record UserPreferenceRecord(MovementKind defaultMovementKind, Integer defaultAccountId,
                                   Integer defaultTargetAccountId, Integer defaultCardId,
                                   Integer defaultExpenseCategoryId, Integer defaultIncomeCategoryId,
                                   Integer defaultInstallmentCount, NotificationPreferenceRecord notifications) {
    public UserPreferenceRecord(UserPreference preference) {
        this(preference.getDefaultMovementKind(), preference.getDefaultAccountId(),
                preference.getDefaultTargetAccountId(), preference.getDefaultCardId(),
                preference.getDefaultExpenseCategoryId(), preference.getDefaultIncomeCategoryId(),
                preference.getDefaultInstallmentCount(),
                new NotificationPreferenceRecord(preference.isNotifyConnectionRequests(),
                        preference.isNotifySharedExpenses(), preference.isNotifyFinancialDigest()));
    }

    public UserPreferenceRecord(MovementKind defaultMovementKind, Integer defaultAccountId,
                                Integer defaultTargetAccountId, Integer defaultCardId,
                                Integer defaultExpenseCategoryId, Integer defaultIncomeCategoryId,
                                Integer defaultInstallmentCount) {
        this(defaultMovementKind, defaultAccountId, defaultTargetAccountId, defaultCardId,
                defaultExpenseCategoryId, defaultIncomeCategoryId, defaultInstallmentCount, null);
    }

    public record NotificationPreferenceRecord(Boolean connectionRequests, Boolean sharedExpenses,
                                               Boolean financialDigest) {
    }
}
