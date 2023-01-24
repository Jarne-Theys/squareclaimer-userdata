#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY user_data/src /home/app/src
COPY user_data/pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/user_data-0.0.1-SNAPSHOT.jar /usr/local/lib/user_data.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/user_data.jar"]