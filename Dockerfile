FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN chmod +x gradlew
RUN ./gradlew build

# Run the JAR (replace with actual jar name or use build/libs/*.jar)
CMD ["java", "-jar", "build/libs/MonkeyDev_API-0.0.1-SNAPSHOT.jar"]
