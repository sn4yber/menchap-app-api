FROM maven:3.9.4-amazoncorretto-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8 -Dproject.reporting.outputEncoding=UTF-8

FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]