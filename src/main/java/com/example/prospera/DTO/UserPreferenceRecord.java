package com.example.prospera.DTO;

import com.example.prospera.Entities.MovementKind;
import com.example.prospera.Entities.UserPreference;

public record UserPreferenceRecord(MovementKind defaultMovementKind, Integer defaultAccountId,
                                   Integer defaultTargetAccountId, Integer defaultCardId,
                                   Integer defaultExpenseCategoryId, Integer defaultIncomeCategoryId,
                                   Integer defaultInstallmentCount) {
    public UserPreferenceRecord(UserPreference preference) {
        this(preference.getDefaultMovementKind(), preference.getDefaultAccountId(),
                preference.getDefaultTargetAccountId(), preference.getDefaultCardId(),
                preference.getDefaultExpenseCategoryId(), preference.getDefaultIncomeCategoryId(),
                preference.getDefaultInstallmentCount());
    }
}
