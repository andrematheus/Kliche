FROM gradle:6.5.1-jdk11 as cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY build.gradle.kts /home/gradle/java-code/
WORKDIR /home/gradle/java-code
RUN gradle clean build --stacktrace --quiet

FROM gradle:6.5.1-jdk11 as builder
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/kliche
WORKDIR /usr/src/kliche
RUN gradle assemble fatJar --stacktrace --quiet

FROM openjdk:11
RUN mkdir -p /srv/kliche/
COPY --from=builder /usr/src/kliche/build/libs/Kliche-1.0-SNAPSHOT-all.jar /srv/kliche/Kliche.jar
RUN mkdir -p /srv/site/
WORKDIR /srv/site/
CMD ["java", "-jar", "/srv/kliche/Kliche.jar", "/srv/site"]

MAINTAINER André Roque Matheus <amatheus@ligpo.dev>