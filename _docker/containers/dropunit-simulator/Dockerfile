FROM openjdk:17

RUN mkdir /application

WORKDIR /application
ENTRYPOINT java -jar target/dropunit-simulator.jar server config/config-it.yml
