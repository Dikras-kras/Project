package ru.weather.chartgenerator.util;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.Document;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Set;

@Component
public class SvgChartExporter {
    private static final Set<String> ALLOWED_NUMERIC_COLUMNS = Set.of(
            "mintemp",
            "maxtemp",
            "rainfall",
            "evaporation",
            "sunshine",
            "windgustspeed",
            "windspeed9am",
            "windspeed3pm",
            "humidity9am",
            "humidity3pm",
            "pressure9am",
            "pressure3pm",
            "cloud9am",
            "cloud3pm",
            "temp9am",
            "temp3pm",
            "risk_mm"
    );

    private static final Set<String> ALLOWED_STRING_COLUMNS = Set.of(
            "windgustdir",
            "winddir9am",
            "winddir3pm",
            "raintoday",
            "raintomorrow"
    );

    public void validateNumericColumn(String columnName) {
        if (!ALLOWED_NUMERIC_COLUMNS.contains(columnName)) {
            throw new IllegalArgumentException("Invalid column: " + columnName);
        }
    }

    public void validateStringColumn(String columnName) {
        if (!ALLOWED_STRING_COLUMNS.contains(columnName)) {
            throw new IllegalArgumentException("Invalid column: " + columnName);
        }
    }

    public String convertChartToSVG(JFreeChart chart, int width, int height) {
        Document document = GenericDOMImplementation.getDOMImplementation()
                .createDocument(null, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        chart.draw(svgGenerator, new java.awt.Rectangle(width, height));

        StringWriter writer = new StringWriter();
        try {
            svgGenerator.stream(writer, true);
        } catch (Exception e) {
            throw new RuntimeException("Error generating SVG", e);
        }

        return writer.toString();
    }
}

