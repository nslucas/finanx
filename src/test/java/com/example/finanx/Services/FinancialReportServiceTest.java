package com.example.finanx.Services;

import com.example.finanx.DTO.ForecastResponse;
import com.example.finanx.Entities.User;
import com.example.finanx.Repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinancialReportServiceTest {
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private RecurringTransactionService recurrenceService;
    @Mock
    private RecurringOccurrenceRepository occurrenceRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardPaymentRepository cardPaymentRepository;
    @Mock
    private ExpenseInstallmentRepository installmentRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountService accountService;

    @Test
    void forecastUsesTwelveMonthsByDefault() {
        User user = new User(1, "Lucas", "Nunes", BigDecimal.valueOf(1000), "lucas@test.com");
        when(authenticatedUserService.getAuthenticatedUser()).thenReturn(user);
        when(accountService.getTotalActiveBalance(1)).thenReturn(BigDecimal.ZERO);
        when(recurrenceService.findActiveUserRecurrencesInRange(any(Integer.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        FinancialReportService service = new FinancialReportService(authenticatedUserService, recurrenceService,
                occurrenceRepository, cardRepository, cardPaymentRepository, installmentRepository,
                transactionRepository, accountService);

        ForecastResponse response = service.getForecast(null);

        assertEquals(12, response.months());
        assertEquals(12, response.forecast().size());
    }
}
