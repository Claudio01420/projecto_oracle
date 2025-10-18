FROM openjdk:22

WORKDIR /app

# Copia el JAR con el nombre estándar "app.jar"
COPY target/MyTodoList-0.0.1-SNAPSHOT.jar app.jar

# (Opcional, sólo si quieres incluir el wallet en la imagen)
# Asegúrate de que la carpeta 'backend/wallet' existe en el repo
COPY wallet/ /app/wallet/
ENV TNS_ADMIN=/app/wallet

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
