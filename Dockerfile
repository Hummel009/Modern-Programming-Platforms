FROM openjdk:11-jre-slim

VOLUME /tmp

EXPOSE 2999

ARG JAR_FILE=appCourse/build/libs/appCourse-all.jar

ADD ${JAR_FILE} course-backend.jar

ENTRYPOINT ["java","-jar","/course-backend.jar"]