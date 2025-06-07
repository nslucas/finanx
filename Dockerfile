# Etapa 1: build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

RUN curl -o wait-for-it.sh https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh && \
    chmod +x wait-for-it.sh

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Instalar netcat e limpar cache
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/finanx-0.0.1-SNAPSHOT.jar .
COPY --from=build /app/wait-for-it.sh .

RUN chmod +x wait-for-it.sh

EXPOSE 8080 5005

ENV JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
ENTRYPOINT ["./wait-for-it.sh", "finanx-db:3306", "--", "java", "-jar", "finanx-0.0.1-SNAPSHOT.jar"]