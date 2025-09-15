package ru.weather.simulator.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.weather.simulator.dto.WeatherDTO;

import java.time.LocalDateTime;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class WeatherService {
    private final Deque<WeatherDTO> weatherHistory = new ConcurrentLinkedDeque<>();
    private final Random random = new Random();

    public WeatherService() {
        for (int i = 0; i < 180; i++) {
            weatherHistory.add(generateWeatherDTO());
        }
    }

    @Scheduled(fixedRate = 1000) // Обновление каждую секунду
    public void updateWeather() {
        if (weatherHistory.size() >= 180) {
            weatherHistory.pollFirst(); // Удаляем старейший элемент
        }
        weatherHistory.addLast(generateWeatherDTO()); // Добавляем новый
    }

    private WeatherDTO generateWeatherDTO() {
        return new WeatherDTO(
                LocalDateTime.now(),
                -10 + random.nextDouble() * 40,  // Температура от -10 до 30 градусов
                20 + random.nextDouble() * 80,   // Влажность от 20% до 100%
                random.nextDouble() * 20         // Скорость ветра до 20 м/с
        );
    }

    public List<WeatherDTO> getWeatherHistory(int limit) {
        return weatherHistory.stream()
                .skip(Math.max(0, weatherHistory.size() - limit)) // Пропускаем старые записи
                .toList(); // Преобразуем в List
    }
}