package ru.weather.chartgenerator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.weather.chartgenerator.service.*;

@RestController
@RequestMapping("/charts")
@Tag(
        name = "API",
        description = """
        Этот API предоставляет возможность генерации различных графиков погоды.

        Numeric параметры:
        - mintemp — минимальная температура.
        - maxtemp — максимальная температура.
        - rainfall — осадки.
        - evaporation — испарение.
        - sunshine — солнечное освещение.
        - windgustspeed — скорость порывов ветра.
        - windspeed9am — скорость ветра в 9:00.
        - windspeed3pm — скорость ветра в 15:00.
        - humidity9am — влажность в 9:00.
        - humidity3pm — влажность в 15:00.
        - pressure9am — давление в 9:00.
        - pressure3pm — давление в 15:00.
        - cloud9am — облачность в 9:00.
        - cloud3pm — облачность в 15:00.
        - temp9am — температура в 9:00.
        - temp3pm — температура в 15:00.
        - risk_mm — риск осадков.

        String параметры:
        - windgustdir — направление порывов ветра.
        - winddir9am — направление ветра в 9:00.
        - winddir3pm — направление ветра в 15:00.
        - raintoday — осадки за сегодня.
        - raintomorrow — осадки за завтра.
    """
)
public class WeatherChartController {
    @Autowired // Для графика с двумя столбцами
    private WeatherComparisonChartService weatherComparisonChartService;
    @Autowired // Для графика тренда по дням
    private DailyTrendChartService dailyTrendChartService;
    @Autowired // для столбчатой диаграммы с усреднёнными значениями по месяцам
    private MonthlyAverageBarChartService monthlyAverageBarChartService;
    @Autowired // для столбчатой диаграммы с усреднёнными значениями по месяцам
    private TextFrequencyChartService TextFrequencyChartService;

    @Operation(
            summary = "График для сравнения двух погодных параметров",
            description = "Отображает два числовых столбца на одном графике. Передайте два Numeric столбца."
    )
    @GetMapping(value = "/{columnName1}/{columnName2}/comparison", produces = "image/svg+xml")
    public String getComparisonChart(@PathVariable String columnName1, @PathVariable String columnName2) {
        return weatherComparisonChartService.generateComparisonChart(columnName1, columnName2);
    }

    @Operation(
            summary = "График тренда по дням",
            description = "Показывает тренд для одного числового столбца без сглаживания, данные представлены по дням."
    )
    @GetMapping(value = "/{columnName}/daily-trend", produces = "image/svg+xml")
    public String getDailyTrendChart(@PathVariable String columnName) {
        return dailyTrendChartService.generateDailyTrendChart(columnName);
    }

    @Operation(
            summary = "Столбчатая диаграмма с усреднёнными значениями по месяцам",
            description = "Отображает усреднённые значения для одного числового столбца по месяцам."
    )
    @GetMapping(value = "/{columnName}/monthly-average-bar", produces = "image/svg+xml")
    public String getMonthlyAverageBarChart(@PathVariable String columnName) {
        return monthlyAverageBarChartService.generateMonthlyAverageBarChart(columnName);
    }

    @Operation(
            summary = "График частоты текстовых параметров",
            description = "Отображает частотную диаграмму для одного текстового столбца."
    )
    @GetMapping(value = "/{columnName}/text-frequency", produces = "image/svg+xml")
    public String getTextFrequencyChart(@PathVariable String columnName) {
        return TextFrequencyChartService.generateTextFrequencyChart(columnName);
    }
}