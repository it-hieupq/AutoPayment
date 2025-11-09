FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/loan-business.jar loan-business.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "loan-business.jar"]
