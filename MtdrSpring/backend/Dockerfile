FROM eclipse-temurin:22-jdk
#COPY pom.xml /tmp/
#COPY src /tmp/src/
WORKDIR /tmp/
EXPOSE 8080
COPY target/MyTodoList-0.0.1-SNAPSHOT.jar MyTodoList.jar
ENV DISABLE_TELEGRAM_BOT=true
ENTRYPOINT ["java","-jar","MyTodoList.jar"]
