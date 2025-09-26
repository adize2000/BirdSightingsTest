# Stage 1: Build the Java application (if you use a build tool like Maven/Gradle)
FROM maven:3-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the final minimal image
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/BirdSightingsAPI-*.jar BirdSightingsAPI.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "BirdSightingsAPI.jar"]