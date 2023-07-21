FROM maven:3.9.3-amazoncorretto-17 AS maven
LABEL MAINTAINER="17746796+sdasda7777@users.noreply.github.com"

# RUN echo "$(ls /usr/lib/jvm/)" && exit 1

WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mvn package


FROM amazoncorretto:17-alpine3.18-jdk

ARG JAR_FILE=endpoint-monitor.jar

WORKDIR /opt/app

COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

ENTRYPOINT ["java", "-jar", "endpoint-monitor.jar"]
