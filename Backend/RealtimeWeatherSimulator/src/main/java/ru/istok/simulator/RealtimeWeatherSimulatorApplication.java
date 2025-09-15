package ru.istok.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RealtimeWeatherSimulatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(RealtimeWeatherSimulatorApplication.class, args);
    }
}
