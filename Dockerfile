FROM eclipse-temurin:8-jdk

WORKDIR /app

# Install Node.js (Node 18 breaks on Java 8 images)
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_16.x | bash - && \
    apt-get install -y nodejs

# Install WebSocket library
RUN npm install ws

# Download Spigot 1.12.2
RUN curl -L -o server.jar https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar

COPY . .

EXPOSE 8080

CMD bash -c "node bridge.js & java -Xms512M -Xmx512M -jar server.jar nogui"
