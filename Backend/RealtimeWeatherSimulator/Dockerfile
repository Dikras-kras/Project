FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/RealtimeWeatherSimulator-0.0.1.jar simulator.jar
ENTRYPOINT ["java", "-jar", "simulator.jar"]