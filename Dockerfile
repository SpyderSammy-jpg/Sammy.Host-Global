FROM eclipse-temurin:8-jdk

WORKDIR /app

# Download Spigot 1.12.2 automatically
RUN curl -L -o server.jar https://download.getbukkit.org/spigot/spigot-1.12.2.jar

COPY . /app

EXPOSE 8080

CMD ["bash", "start.sh"]
