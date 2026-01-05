# Build Stage
FROM gradle:jdk21-jammy AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Create uploads directory
RUN mkdir -p /app/uploads

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
