FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} scheduler-service.jar
ENTRYPOINT ["java","-jar","/scheduler-service.jar"]