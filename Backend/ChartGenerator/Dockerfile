FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/ChartGenerator-0.0.1.jar generator.jar
ENTRYPOINT ["java", "-jar", "generator.jar"]