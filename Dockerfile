FROM openjdk:8-jre-alpine
EXPOSE 8080
COPY ./target/devops-maven-*.jar /usr/app/
WORKDIR /usr/app
CMD java -jar devops-maven-*.jar