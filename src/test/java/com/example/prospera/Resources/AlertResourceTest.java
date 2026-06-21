package com.example.prospera.Resources;

import com.example.prospera.DTO.AlertRecord;
import com.example.prospera.Entities.AlertSeverity;
import com.example.prospera.Entities.AlertType;
import com.example.prospera.Services.AlertService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AlertResourceTest {

    @Test
    void findAlertsDelegatesQueryParametersToService() {
        AlertService service = mock(AlertService.class);
        LocalDate from = LocalDate.of(2026, 6, 18);
        LocalDate to = LocalDate.of(2026, 6, 25);
        AlertRecord alert = new AlertRecord("LOW_ACCOUNT_BALANCE:account:1:2026-6",
                AlertType.LOW_ACCOUNT_BALANCE, AlertSeverity.WARNING, "Low balance",
                "ACCOUNT", 1, BigDecimal.valueOf(99), BigDecimal.valueOf(100),
                null, null, 6, 2026);
        when(service.getAlerts(6, 2026, from, to)).thenReturn(List.of(alert));

        AlertResource resource = new AlertResource(service);

        List<AlertRecord> response = resource.findAlerts(6, 2026, from, to).getBody();

        assertEquals(List.of(alert), response);
        verify(service).getAlerts(6, 2026, from, to);
    }

    @Test
    void findAlertsAllowsDefaultParameters() {
        AlertService service = mock(AlertService.class);
        when(service.getAlerts(null, null, null, null)).thenReturn(List.of());

        AlertResource resource = new AlertResource(service);

        List<AlertRecord> response = resource.findAlerts(null, null, null, null).getBody();

        assertEquals(List.of(), response);
        verify(service).getAlerts(null, null, null, null);
    }
}
