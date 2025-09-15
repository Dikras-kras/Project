package ru.istok.generator.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.istok.generator.util.SvgChartExporter;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Service
public class WeatherComparisonChartService {
    private final SvgChartExporter svgChartExporter;
    private final JdbcTemplate jdbcTemplate;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public WeatherComparisonChartService(JdbcTemplate jdbcTemplate, SvgChartExporter svgChartExporter) {
        this.jdbcTemplate = jdbcTemplate;
        this.svgChartExporter = svgChartExporter;
    }

    public String generateComparisonChart(String column1, String column2) {
        svgChartExporter.validateNumericColumn(column1);
        svgChartExporter.validateNumericColumn(column2);

        // Датасет для графика
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int recordsPerMonth = 25; // Ожидаемое количество записей в месяц

        // Генерация данных для графика
        for (int month = 0; month < 12; month++) {
            int offset = month * recordsPerMonth;

            // Запрашиваем значения для двух столбцов в текущем месяце
            String sql = "SELECT " + column1 + ", " + column2 + " FROM observations LIMIT ? OFFSET ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, recordsPerMonth, offset);

            if (!results.isEmpty()) {
                double avgColumn1 = results.stream()
                        .mapToDouble(row -> ((Number) row.get(column1)).doubleValue())
                        .average()
                        .orElse(0);

                double avgColumn2 = results.stream()
                        .mapToDouble(row -> ((Number) row.get(column2)).doubleValue())
                        .average()
                        .orElse(0);

                dataset.addValue(avgColumn1, column1, String.valueOf(month + 1));
                dataset.addValue(avgColumn2, column2, String.valueOf(month + 1));
            }
        }

        // Создаем диаграмму
        JFreeChart chart = createChart(dataset, column1, column2);
        return svgChartExporter.convertChartToSVG(chart, WIDTH, HEIGHT);
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset, String column1, String column2) {
        JFreeChart chart = ChartFactory.createBarChart(
                "Average Monthly " + column1 + " and " + column2,
                "Month",
                "Value",
                dataset
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainAxis(new CategoryAxis("Month"));
        plot.setRangeAxis(new NumberAxis("Value"));

        BarRenderer renderer = new BarRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // Первый столбец (синий)
        renderer.setSeriesPaint(1, new Color(192, 80, 77));  // Второй столбец (красный)
        plot.setRenderer(renderer);

        return chart;
    }
}
