FROM amazoncorretto:17.0.7-alpine

COPY build/libs/reports-service-app.jar /app.jar

EXPOSE 8082

CMD ["java", "-jar", "/app.jar"]