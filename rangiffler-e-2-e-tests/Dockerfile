FROM eclipse-temurin:21-jdk

WORKDIR /rangiffler
ENV TZ=UTC+3
COPY ./gradle ./gradle
COPY ./rangiffler-e-2-e-tests ./rangiffler-e-2-e-tests
COPY ./rangiffler-grpc-common ./rangiffler-grpc-common
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD ./gradlew test -Dtest.env=docker -Drepository=jpa -Duser.timezone=UTC+4
