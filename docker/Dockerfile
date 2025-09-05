FROM maven:3-eclipse-temurin-21-alpine AS builder

WORKDIR /app
COPY ./pom.xml pom.xml
RUN mvn clean dependency:go-offline

COPY ./src src
RUN mvn package -DskipTests

RUN jar xf $(find target -type f -name "*.jar" | head -n 1)
RUN jdeps  \
    --recursive  \
    --ignore-missing-deps  \
    --print-module-deps  \
    --multi-release 21  \
    --class-path 'BOOT-INF/lib/*' \
    target/*.jar >> jdeps.txt
RUN jlink \
    --add-modules $(cat jdeps.txt) \
    --no-header-files \
    --no-man-pages \
    --strip-debug \
    --output /opt/jre

FROM alpine:3.21.3
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=builder /opt/jre /opt/jre
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["/opt/jre/bin/java", "-jar", "app.jar"]