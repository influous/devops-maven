FROM openjdk:8-jre-alpine
EXPOSE 8080
COPY ./target/devops-maven-1.0-SNAPSHOT.jar /usr/app
WORKDIR /usr/app
ENTRYPOINT ["java, "-jar", "devops-maven-1.0-SNAPSHOT.jar"]