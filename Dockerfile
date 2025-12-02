# Dockerfile for the Monkey Dev project

# Use the latest Ubuntu LTS as the base image
FROM ubuntu:22.04

# Set the working directory in the container
WORKDIR /app

# Install the necessary dependencies
RUN apt-get update && apt-get install -y openjdk-21-jdk openjdk-21-jre

# Set the JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

# Set the PATH environment variable
ENV PATH=$PATH:$JAVA_HOME/bin

# Copy the project files to the container
COPY . .

# Make sure gradlew has execute permissions
RUN chmod +x ./gradlew

# Build the project
RUN ./gradlew build

# Set the default command to run the application
CMD ["./gradlew", "bootRun"]