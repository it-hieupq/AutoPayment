FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/loan-business.jar loan-business.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "loan-business.jar"]
