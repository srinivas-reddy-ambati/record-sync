FROM maven:3.9.3-eclipse-temurin-11 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:11-jre
WORKDIR /app

COPY --from=build /app/target/sync-service-1.0-shaded.jar app.jar

CMD ["java", "-jar", "app.jar"]