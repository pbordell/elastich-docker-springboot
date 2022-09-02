FROM docker-registry.ctti.extranet.gencat.cat/gencatcloud/java:11-openjdk

VOLUME /tmp

COPY maven/galileu-searcher.war ${APP_DIR}/app.war

COPY run.sh /entrypoint.sh

USER root
COPY elasticPre.pem $JAVA_HOME/lib/security
COPY elasticPro.pem $JAVA_HOME/lib/security

RUN \
    cd $JAVA_HOME/lib/security \
    && keytool -keystore cacerts -storepass changeit -noprompt -trustcacerts -import -alias elasticPrecert -file elasticPre.pem
RUN \
    cd $JAVA_HOME/lib/security \
    && keytool -keystore cacerts -storepass changeit -noprompt -trustcacerts -import -alias elasticProcert -file elasticPro.pem


RUN chmod +x /entrypoint.sh

CMD ["/entrypoint.sh"]
