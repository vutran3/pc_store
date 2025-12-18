
# Start with a Maven image that includes JDK 21
FROM maven:3.9.9-amazoncorretto-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with maven
RUN mvn package -DskipTests -Dspotless.check.skip=true


# Start with Amazon Correto JDK 21
FROM amazoncorretto:21.0.4

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8282

ENTRYPOINT ["java", "-jar", "app.jar"]
