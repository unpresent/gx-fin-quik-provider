FROM openjdk:17-jdk-alpine3.14
COPY target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
