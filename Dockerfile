FROM xldevops/jdk17-lts

ARG JAR_FILE=build/libs/reports-service-app.jar

#RUN mkdir /app

COPY ${JAR_FILE} /spring-boot-application.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar","/spring-boot-application.jar"]
