# Use lightweight Java image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file (make sure you build it first)
COPY target/taskmanager-0.0.1-SNAPSHOT.jar app.jar

# Run application
ENTRYPOINT ["java","-jar","app.jar"]