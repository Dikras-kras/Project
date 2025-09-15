package ru.weather.chartgenerator.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.weather.chartgenerator.util.SvgChartExporter;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Service
public class DailyTrendChartService {
    private final JdbcTemplate jdbcTemplate;
    private final SvgChartExporter svgChartExporter;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public DailyTrendChartService(JdbcTemplate jdbcTemplate, SvgChartExporter svgChartExporter) {
        this.jdbcTemplate = jdbcTemplate;
        this.svgChartExporter = svgChartExporter;
    }

    public String generateDailyTrendChart(String columnName) {
        svgChartExporter.validateNumericColumn(columnName);

        XYSeries series = new XYSeries(columnName);
        String sql = "SELECT " + columnName + " FROM observations";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        int index = 1;
        for (Map<String, Object> row : results) {
            double value = ((Number) row.get(columnName)).doubleValue();
            series.add(index++, value);
        }

        JFreeChart chart = createChart(series, columnName);
        return svgChartExporter.convertChartToSVG(chart, WIDTH, HEIGHT);
    }

    private JFreeChart createChart(XYSeries series, String columnName) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                columnName + " Over Time",
                "Index",
                columnName,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new NumberAxis("Days"));
        plot.setRangeAxis(new NumberAxis(columnName));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        renderer.setSeriesPaint(0, new Color(0, 102, 255));
        plot.setRenderer(renderer);

        return chart;
    }
}
