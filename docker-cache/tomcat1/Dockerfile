FROM tomcat:9.0.34-jdk11
LABEL maintainer="volbase@volantetech.com"
ENV TOMCAT_HOME /usr/local/tomcat
RUN mkdir -p $TOMCAT_HOME/logs/log4j2logs
COPY --chown=root webapps/. $TOMCAT_HOME/webapps/
COPY --chown=root start.sh $TOMCAT_HOME/bin/start.sh
