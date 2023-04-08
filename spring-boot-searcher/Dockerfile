FROM openjdk:11

VOLUME /tmp

COPY target/spring-boot-searcher.war ${APP_DIR}/searcher.war

CMD java -jar ${APP_DIR}/searcher.war
