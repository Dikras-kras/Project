package ru.weather.simulator.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeatherDTO {
    private final LocalDateTime timestamp;
    private final double temperature;
    private final double humidity;
    private final double windSpeed;

    public WeatherDTO(LocalDateTime timestamp, double temperature, double humidity, double windSpeed) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }
}
