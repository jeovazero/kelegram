FROM ghcr.io/graalvm/graalvm-ce:ol8-java11-22.0.0.2-b2 as builder

WORKDIR /app

RUN gu install native-image

COPY . /app


# Build the app (via Maven, Gradle, etc) and create the native image
RUN ./gradlew :server:nativeBuild

FROM ghcr.io/linuxcontainers/debian-slim:11.3

COPY --from=builder /app/server/build/native/nativeBuild/server /server

CMD ["/server"]