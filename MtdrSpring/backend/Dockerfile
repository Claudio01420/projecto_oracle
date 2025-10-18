FROM openjdk:22
#COPY pom.xml /tmp/
#COPY src /tmp/src/
WORKDIR /app
COPY target/MyTodoList-0.0.1-SNAPSHOT.jar MyTodoList.jar
COPY wallet/ /app/wallet/
ENV TNS_ADMIN=/app/wallet
EXPOSE 8080
ENTRYPOINT ["java","-Doracle.net.tns_admin=/app/wallet","-jar","/app/app.jar"]
