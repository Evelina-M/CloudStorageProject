FROM openjdk:17
WORKDIR /app
EXPOSE 9090
ADD target/CloudStorageProject-0.0.1-SNAPSHOT.jar cloud-storage-project-back.jar
ENTRYPOINT ["java", "-jar", "cloud-storage-project-back.jar"]