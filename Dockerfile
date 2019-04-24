FROM maven:3.6.1-jdk-11
ENV PKCS8_PATH /root/registry/ssl/hub.c.pkcs8
ENV CERT_PATH /root/registry/ssl/hub.c.der
VOLUME /tmp
COPY target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
