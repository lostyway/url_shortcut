# Этап 1 - сборка проекта в jar
FROM maven:3.8-openjdk-17 as maven
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn package -DskipTests

# Этап 2 - указание как запустить проект
FROM openjdk:17.0.2-jdk
WORKDIR /app
COPY --from=maven /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]