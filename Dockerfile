FROM adoptopenjdk:17-jre-hotspot

COPY build/libs/reports-service-app.jar /app.jar

EXPOSE 8082

CMD ["java", "-jar", "/app.jar"]