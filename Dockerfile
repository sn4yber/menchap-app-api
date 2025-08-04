FROM maven:3.9.4-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /app
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring
COPY --from=build /app/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]