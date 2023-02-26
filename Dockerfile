FROM openjdk:11
EXPOSE 8888
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} genx-minio-0.0.1.jar
ENTRYPOINT ["java","-jar","/genx-minio-0.0.1.jar"]
