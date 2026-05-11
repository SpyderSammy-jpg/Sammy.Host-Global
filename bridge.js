const WebSocket = require("ws");
const net = require("net");

const server = new WebSocket.Server({ port: 8080 });

server.on("connection", ws => {
    const mc = net.connect(25565, "127.0.0.1");

    ws.on("message", msg => mc.write(msg));
    mc.on("data", data => ws.send(data));

    ws.on("close", () => mc.end());
    mc.on("end", () => ws.close());
});
