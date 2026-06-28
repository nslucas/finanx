package com.example.prospera.Services;

import com.example.prospera.Entities.Card;
import com.example.prospera.Entities.CardStatementStatus;
import com.example.prospera.Entities.User;
import com.example.prospera.repositories.CardPaymentRepository;
import com.example.prospera.repositories.ExpenseInstallmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardStatementServiceTest {
    @Mock
    private CardService cardService;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private ExpenseInstallmentRepository installmentRepository;
    @Mock
    private CardPaymentRepository cardPaymentRepository;

    @Test
    void unpaidStatementIsOpen() {
        assertEquals(CardStatementStatus.OPEN,
                CardStatementService.resolveStatus(BigDecimal.valueOf(100), BigDecimal.ZERO));
    }

    @Test
    void lowerPaymentIsPartial() {
        assertEquals(CardStatementStatus.PARTIALLY_PAID,
                CardStatementService.resolveStatus(BigDecimal.valueOf(100), BigDecimal.valueOf(40)));
    }

    @Test
    void exactPaymentIsPaid() {
        assertEquals(CardStatementStatus.PAID,
                CardStatementService.resolveStatus(BigDecimal.valueOf(100), BigDecimal.valueOf(100)));
    }

    @Test
    void higherPaymentIsOverpaid() {
        assertEquals(CardStatementStatus.OVERPAID,
                CardStatementService.resolveStatus(BigDecimal.valueOf(100), BigDecimal.valueOf(120)));
    }

    @Test
    void availableLimitUsesUpcomingCommittedInstallmentsNotOnlyStatementMonth() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.ZERO, "lucas@test.com");
        Card card = new Card(2, "Nubank", "Nubank", "Mastercard", "1234",
                BigDecimal.valueOf(2800), 29, 10, true, 1);
        CardStatementService service = new CardStatementService(cardService, authenticatedUserService,
                installmentRepository, cardPaymentRepository);

        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(cardService.findUserCard(1, 2)).thenReturn(card);
        when(installmentRepository.findCardStatementInstallmentRecords(1, 2,
                java.time.LocalDate.of(2026, 7, 1), java.time.LocalDate.of(2026, 8, 1)))
                .thenReturn(List.of());
        when(installmentRepository.sumCardStatementInDueDateRange(1, 2,
                java.time.LocalDate.of(2026, 7, 1), java.time.LocalDate.of(2026, 8, 1)))
                .thenReturn(BigDecimal.valueOf(250));
        when(cardPaymentRepository.sumByUserIdCardIdAndMonthYear(1, 2, 7, 2026))
                .thenReturn(BigDecimal.ZERO);
        when(installmentRepository.sumCardCommittedLimitFrom(1, 2, java.time.LocalDate.of(2026, 7, 1)))
                .thenReturn(BigDecimal.valueOf(2500));
        when(cardPaymentRepository.sumByUserIdAndCardIdFromMonthYear(1, 2, 7, 2026))
                .thenReturn(BigDecimal.ZERO);

        var response = service.getStatement(2, 7, 2026);

        assertEquals(BigDecimal.valueOf(300), response.availableLimit());
    }
}
