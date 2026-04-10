FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew build

# Run the JAR produced by Gradle
CMD ["java", "-jar", "build/libs/ezcart_api-0.0.1-SNAPSHOT.jar"]
