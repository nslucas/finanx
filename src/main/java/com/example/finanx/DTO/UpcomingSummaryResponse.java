package com.example.finanx.DTO;

import java.time.LocalDate;
import java.util.List;

public record UpcomingSummaryResponse(LocalDate from, LocalDate to,
                                      List<RecurringOccurrenceRecord> recurrenceOccurrences,
                                      List<UpcomingCardStatementRecord> cardStatements) {
}
