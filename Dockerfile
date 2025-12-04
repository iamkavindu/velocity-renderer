# syntax=docker/dockerfile:1.4

# ============================================================================
# Builder Stage: Compile application with Vaadin frontend
# ============================================================================
FROM bellsoft/liberica-openjdk-alpine:25 AS builder

RUN apk add --no-cache nodejs npm

WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw clean package -Pproduction -DskipTests && \
    mv target/velocity-renderer-*.jar target/app.jar

# ============================================================================
# Runtime Stage: Liberica JRE 25 Alpine
# ============================================================================
FROM bellsoft/liberica-openjre-alpine:25

LABEL org.opencontainers.image.title="Velocity Renderer" \
      org.opencontainers.image.description="Spring Boot + Vaadin application for Apache Velocity template rendering" \
      org.opencontainers.image.vendor="dev.iamkavindu" \
      maintainer="dev.iamkavindu"

WORKDIR /app

COPY --from=builder /build/target/app.jar ./app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
