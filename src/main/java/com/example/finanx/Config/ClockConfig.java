package com.example.finanx.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {
    private static final String TIME_ZONE = "America/Sao_Paulo";

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(TIME_ZONE));
    }
}
