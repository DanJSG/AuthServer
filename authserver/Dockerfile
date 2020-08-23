FROM openjdk:13
COPY /target/authentity-docker.jar /usr/build/authentity-docker.jar
EXPOSE 8090
WORKDIR /usr/build
ENTRYPOINT [ "java", "-jar", "authentity-docker.jar" ]
