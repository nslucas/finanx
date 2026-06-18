package com.example.finanx.DTO;

import java.util.List;

public record ForecastResponse(Integer months, List<ForecastMonthRecord> forecast) {
}
