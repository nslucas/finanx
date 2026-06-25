package com.example.prospera.Services;

import com.example.prospera.DTO.UserPreferenceRecord;
import com.example.prospera.Entities.*;
import com.example.prospera.Exceptions.ObjectNotFoundException;
import com.example.prospera.repositories.AccountRepository;
import com.example.prospera.repositories.CardRepository;
import com.example.prospera.repositories.CategoryRepository;
import com.example.prospera.repositories.UserPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceTest {
    @Mock
    private UserPreferenceRepository preferenceRepository;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void findPreferencesReturnsSafeDefaultsWhenUserHasNoSavedPreference() {
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user());
        when(preferenceRepository.findByUserId(1)).thenReturn(Optional.empty());

        UserPreferenceRecord record = service().findAuthenticatedUserPreferences();

        assertEquals(MovementKind.CARD_EXPENSE, record.defaultMovementKind());
        assertEquals(1, record.defaultInstallmentCount());
        assertNull(record.defaultAccountId());
        assertNull(record.defaultCardId());
        verify(preferenceRepository, never()).save(any());
    }

    @Test
    void updatePreferencesCreatesPreferenceForAuthenticatedUser() {
        UserPreferenceRecord request = new UserPreferenceRecord(MovementKind.EXPENSE, 10, null, 20, 30, 40, 3);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user());
        when(preferenceRepository.findByUserId(1)).thenReturn(Optional.empty());
        when(accountRepository.findByIdAndUserId(10, 1)).thenReturn(Optional.of(account(10, true)));
        when(cardRepository.findByIdAndUserId(20, 1)).thenReturn(Optional.of(card(20, true)));
        when(categoryRepository.findByIdAndUserId(30, 1)).thenReturn(Optional.of(category(30, CategoryType.EXPENSE, true)));
        when(categoryRepository.findByIdAndUserId(40, 1)).thenReturn(Optional.of(category(40, CategoryType.INCOME, true)));
        when(preferenceRepository.save(any(UserPreference.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserPreferenceRecord response = service().updateAuthenticatedUserPreferences(request);

        ArgumentCaptor<UserPreference> captor = ArgumentCaptor.forClass(UserPreference.class);
        verify(preferenceRepository).save(captor.capture());
        assertEquals(1, captor.getValue().getUserId());
        assertEquals(MovementKind.EXPENSE, response.defaultMovementKind());
        assertEquals(10, response.defaultAccountId());
        assertEquals(20, response.defaultCardId());
        assertEquals(3, response.defaultInstallmentCount());
    }

    @Test
    void updatePreferencesRejectsResourceFromAnotherUser() {
        UserPreferenceRecord request = new UserPreferenceRecord(MovementKind.EXPENSE, 10, null, null, null, null, 1);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user());
        when(accountRepository.findByIdAndUserId(10, 1)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> service().updateAuthenticatedUserPreferences(request));
        verify(preferenceRepository, never()).save(any());
    }

    @Test
    void updatePreferencesRejectsWrongCategoryType() {
        UserPreferenceRecord request = new UserPreferenceRecord(MovementKind.CARD_EXPENSE, null, null, null, 30, null, 1);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user());
        when(categoryRepository.findByIdAndUserId(30, 1)).thenReturn(Optional.of(category(30, CategoryType.INCOME, true)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service().updateAuthenticatedUserPreferences(request));

        assertEquals("Default expense category type must be EXPENSE", exception.getMessage());
        verify(preferenceRepository, never()).save(any());
    }

    @Test
    void clearAccountPreferenceRemovesSourceAndTargetDefaults() {
        UserPreference preference = new UserPreference(5, 1, MovementKind.TRANSFER, 10, 10, null, null, null, 1);
        when(preferenceRepository.findByUserId(1)).thenReturn(Optional.of(preference));

        service().clearAccountPreference(1, 10);

        assertNull(preference.getDefaultAccountId());
        assertNull(preference.getDefaultTargetAccountId());
        verify(preferenceRepository).save(preference);
    }

    @Test
    void findPreferencesClearsInactiveSavedReferences() {
        UserPreference preference = new UserPreference(5, 1, MovementKind.CARD_EXPENSE, 10, null, 20, 30, null, 1);
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user());
        when(preferenceRepository.findByUserId(1)).thenReturn(Optional.of(preference));
        when(accountRepository.findByIdAndUserId(10, 1)).thenReturn(Optional.of(account(10, false)));
        when(cardRepository.findByIdAndUserId(20, 1)).thenReturn(Optional.of(card(20, true)));
        when(categoryRepository.findByIdAndUserId(30, 1)).thenReturn(Optional.of(category(30, CategoryType.EXPENSE, false)));
        when(preferenceRepository.save(any(UserPreference.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserPreferenceRecord record = service().findAuthenticatedUserPreferences();

        assertNull(record.defaultAccountId());
        assertEquals(20, record.defaultCardId());
        assertNull(record.defaultExpenseCategoryId());
        verify(preferenceRepository).save(preference);
    }

    private UserPreferenceService service() {
        return new UserPreferenceService(preferenceRepository, authenticatedUserService, accountRepository,
                cardRepository, categoryRepository);
    }

    private User user() {
        return new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
    }

    private Account account(Integer id, Boolean active) {
        return new Account(id, "Checking", AccountType.CHECKING, BigDecimal.valueOf(1000), "BRL", active, 1);
    }

    private Card card(Integer id, Boolean active) {
        return new Card(id, "Nubank", "Purple", "Mastercard", "1234",
                BigDecimal.valueOf(5000), 25, 10, active, 1);
    }

    private Category category(Integer id, CategoryType type, Boolean active) {
        return new Category(id, type.name(), type, active, 1);
    }
}
