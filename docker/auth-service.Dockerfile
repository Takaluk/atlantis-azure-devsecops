FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle build.gradle ./
COPY auth-service auth-service
COPY stock-service stock-service
COPY news-service news-service
COPY frontend frontend

RUN chmod +x gradlew
RUN ./gradlew :auth-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/auth-service/build/libs /tmp/libs
RUN JAR_FILE="$(find /tmp/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)" \
    && cp "${JAR_FILE}" /app/app.jar \
    && rm -rf /tmp/libs

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
