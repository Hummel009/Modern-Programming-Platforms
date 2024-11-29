FROM openjdk:11-jdk-alpine

VOLUME /tmp

EXPOSE 2999

ARG JAR_FILE=appCourse/build/libs/appCourse-24.11.29.jar

ADD ${JAR_FILE} course-backend.jar

ENTRYPOINT ["java","-jar","/course-backend.jar"]