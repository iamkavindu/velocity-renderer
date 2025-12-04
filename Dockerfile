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
RUN ./mvnw clean package -Pproduction -DskipTests -Dvaadin.ci.build=true && \
    mv target/velocity-renderer-*.jar target/app.jar

# ============================================================================
# Runtime Stage: Liberica JRE 25 Alpine
# ============================================================================
FROM bellsoft/liberica-openjre-alpine:25

LABEL org.opencontainers.image.title="Velocity Renderer" \
      org.opencontainers.image.description="Spring Boot + Vaadin application for Apache Velocity template rendering" \
      org.opencontainers.image.vendor="dev.iamkavindu" \
      maintainer="dev.iamkavindu"

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

COPY --from=builder --chown=appuser:appgroup /build/target/app.jar ./app.jar

# Switch to non-root user
USER appuser

# Set production profile and JVM options optimized for containers
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport \
                       -XX:MaxRAMPercentage=75.0 \
                       -XX:InitialRAMPercentage=50.0 \
                       -XX:+ExitOnOutOfMemoryError \
                       -XX:+UseG1GC \
                       -XX:+UseStringDeduplication \
                       -Djava.security.egd=file:/dev/./urandom \
                       -Dfile.encoding=UTF-8"

EXPOSE 8080

# Health check for container orchestration
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
