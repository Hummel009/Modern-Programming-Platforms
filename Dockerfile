FROM eclipse-temurin:11-alpine

EXPOSE 2999

VOLUME /tmp

ARG JAR_FILE=appCourse/build/libs/appCourse-all.jar

ADD ${JAR_FILE} course-backend.jar

ENTRYPOINT ["java","-jar","/course-backend.jar"]