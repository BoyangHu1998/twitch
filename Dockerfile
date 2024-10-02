FROM tomcat:11.0-jdk21
LABEL maintainer="boyanghu98@gmail.com"

COPY ./twitch-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Expose the default Tomcat port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]