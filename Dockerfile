FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

# Compile the Java proxy
RUN javac Proxy.java

# Download Spigot 1.12.2
RUN curl -L -o server.jar https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar

EXPOSE 8080

CMD bash -c "java Proxy & java -Xms512M -Xmx512M -jar server.jar nogui"
