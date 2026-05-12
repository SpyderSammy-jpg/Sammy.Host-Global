import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Base64;

public class Proxy {
    public static void main(String[] args) throws Exception {
        int listenPort = 8080;
        String backendHost = "127.0.0.1";
        int backendPort = 25565;

        ServerSocket server = new ServerSocket(listenPort);
        System.out.println("WebSocket proxy running on port " + listenPort);

        while (true) {
            Socket client = server.accept();
            new Thread(() -> handle(client, backendHost, backendPort)).start();
        }
    }

    static void handle(Socket client, String backendHost, int backendPort) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();

            String line;
            String key = null;

            while (!(line = in.readLine()).isEmpty()) {
                if (line.startsWith("Sec-WebSocket-Key:")) {
                    key = line.split(": ")[1];
                }
            }

            if (key == null) {
                client.close();
                return;
            }

            String accept = Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1")
                    .digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes())
            );

            String response =
                "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + accept + "\r\n\r\n";

            out.write(response.getBytes());
            out.flush();

            Socket backend = new Socket(backendHost, backendPort);

            new Thread(() -> forward(client, backend)).start();
            new Thread(() -> forward(backend, client)).start();

        } catch (Exception e) {
            try { client.close(); } catch (Exception ignored) {}
        }
    }

    static void forward(Socket from, Socket to) {
        try {
            InputStream is = from.getInputStream();
            OutputStream os = to.getOutputStream();
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
                os.flush();
            }
        } catch (Exception ignored) {}
        try { from.close(); } catch (Exception ignored) {}
        try { to.close(); } catch (Exception ignored) {}
    }
}
