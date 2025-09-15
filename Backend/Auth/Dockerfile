FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/Auth-0.0.1.jar auth.jar
ENTRYPOINT ["java", "-jar", "auth.jar"]