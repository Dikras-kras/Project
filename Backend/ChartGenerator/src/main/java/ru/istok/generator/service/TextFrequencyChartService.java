package ru.istok.generator.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.istok.generator.util.SvgChartExporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TextFrequencyChartService {
    private final JdbcTemplate jdbcTemplate;
    private final SvgChartExporter svgChartExporter;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public TextFrequencyChartService(JdbcTemplate jdbcTemplate, SvgChartExporter svgChartExporter) {
        this.jdbcTemplate = jdbcTemplate;
        this.svgChartExporter = svgChartExporter;
    }

    public String generateTextFrequencyChart(String columnName) {
        svgChartExporter.validateStringColumn(columnName);

        // Создаем карту для подсчета вхождений слов
        Map<String, Integer> wordFrequency = new HashMap<>();

        // Подсчитываем вхождения слов за весь год
        String sql = "SELECT " + columnName + " FROM observations";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        // Подсчитываем вхождения слов за весь год
        for (Map<String, Object> row : results) {
            String value = (String) row.get(columnName);
            if (value != null && !value.isEmpty()) {
                String[] words = value.split("\\s+"); // Разделение по пробелам
                for (String word : words) {
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            }
        }

        // Создаем диаграмму на основе подсчитанных данных
        JFreeChart chart = createChart(wordFrequency, columnName);

        // Конвертируем диаграмму в SVG
        return svgChartExporter.convertChartToSVG(chart, WIDTH, HEIGHT);
    }

    private JFreeChart createChart(Map<String, Integer> wordFrequency, String columnName) {
        // Создаем набор данных для столбчатой диаграммы
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        wordFrequency.forEach((word, count) -> {
            dataset.addValue(count, "Frequency", word);
        });

        // Создаем столбчатую диаграмму
        JFreeChart chart = ChartFactory.createBarChart(
                "Word Frequency in " + columnName,
                "Word",
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Настройка визуального отображения
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new java.awt.Color(0, 123, 255)); // Цвет столбцов (синий)

        // Настройка оси X
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12)); // Увеличение шрифта для подписей
        domainAxis.setTickLabelFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 10)); // Уменьшение шрифта для меток

        // Поворот подписей на 45 градусов
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(45.0f));

        // Увеличение области для отображения подписей оси X
        domainAxis.setLowerMargin(0.05);  // Увеличение отступа слева
        domainAxis.setUpperMargin(0.05);  // Увеличение отступа справа

        return chart;
    }
}
