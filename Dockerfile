FROM tomcat:11.0-jdk21
LABEL maintainer="boyanghu98@gmail.com"

COPY ./twitch-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

COPY ./keystore.jks /usr/local/tomcat/conf/
COPY ./server.xml /usr/local/tomcat/conf/

# Expose the default Tomcat port
EXPOSE 8080 8443

# Start Tomcat
CMD ["catalina.sh", "run"]


# sudo docker run -p 8080:8080 -p 8443:8443 twitch
# sudo docker run -p 8080:8080 -p 8443:8443 -v /etc/letsencrypt/live/ct-hb.work.gd/keystore.jks:/usr/local/tomcat/conf/keystore.jks twitch