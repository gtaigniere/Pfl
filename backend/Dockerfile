ARG JDK_IMAGE
ARG JRE_VERSION
ARG APP_NAME

FROM eclipse-temurin:${JDK_IMAGE} as base
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src


FROM base as maven_test
CMD ["./mvnw", "test"]


FROM base as development
RUN apt-get update && apt-get -y upgrade
RUN apt-get install -y inotify-tools dos2unix
CMD ./run.sh


FROM base as maven_build
RUN ./mvnw package


FROM eclipse-temurin:${JRE_VERSION} as production
EXPOSE 8080
COPY --from=maven_build "$HOME/target/$APP_NAME-*.jar" "/$APP_NAME.jar"
CMD java -Djava.security.egd=file:/dev/./urandom -jar $APP_NAME.jar
