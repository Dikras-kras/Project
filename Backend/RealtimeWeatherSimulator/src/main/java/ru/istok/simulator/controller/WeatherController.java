package ru.istok.simulator.controller;

import org.springframework.web.bind.annotation.*;
import ru.istok.simulator.dto.WeatherDTO;
import ru.istok.simulator.service.WeatherService;

import java.util.List;

@RestController
@RequestMapping("/realtime-weather-simulator")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/latest")
    public List<WeatherDTO> getLatestWeather(@RequestParam(defaultValue = "180") int limit) {
        return weatherService.getWeatherHistory(limit);
    }
}
