package ru.istok.simulator.dto;

import java.time.LocalDateTime;

public record WeatherDTO(
        LocalDateTime timestamp,
        double temperature,
        double humidity,
        double windSpeed
) {}
