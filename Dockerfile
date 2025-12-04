# syntax=docker/dockerfile:1.4

# ============================================================================
# Builder Stage: Compile application with Vaadin frontend
# ============================================================================
FROM eclipse-temurin:25-jdk AS builder

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl ca-certificates binutils && \
    curl -fsSL https://deb.nodesource.com/setup_22.x | bash - && \
    apt-get install -y --no-install-recommends nodejs && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src/ src/
RUN ./mvnw clean package -Pproduction -DskipTests && \
    mv target/velocity-renderer-*.jar target/app.jar

WORKDIR /build/target
RUN mkdir extracted && cd extracted && jar -xf ../app.jar

RUN jdeps \
      --ignore-missing-deps \
      --print-module-deps \
      --multi-release 25 \
      -q \
      --recursive \
      --class-path "extracted/BOOT-INF/lib/*" \
      extracted/BOOT-INF/classes \
      > modules.txt 2>/dev/null || \
    echo "java.base,java.logging,java.xml,java.desktop,java.management,java.naming,java.sql,java.net.http,java.security.jgss,jdk.unsupported,jdk.crypto.ec" > modules.txt

RUN jlink \
      --add-modules $(cat modules.txt) \
      --strip-debug \
      --no-man-pages \
      --no-header-files \
      --compress=zip-9 \
      --output /javaruntime

# ============================================================================
# Runtime Stage: Google Distroless Java 25
# ============================================================================
FROM gcr.io/distroless/base-debian12:nonroot

LABEL org.opencontainers.image.title="Velocity Renderer" \
      org.opencontainers.image.description="Spring Boot + Vaadin application for Apache Velocity template rendering" \
      org.opencontainers.image.vendor="dev.iamkavindu" \
      maintainer="dev.iamkavindu"

WORKDIR /app

COPY --from=builder /javaruntime /opt/java
COPY --from=builder /build/target/app.jar ./app.jar

ENV JAVA_HOME=/opt/java \
    PATH="/opt/java/bin:${PATH}" \
    JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

ENTRYPOINT ["/opt/java/bin/java", "-jar", "/app/app.jar"]
