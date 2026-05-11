import java.net.*;
import java.io.*;

public class Proxy {
    public static void main(String[] args) throws Exception {
        int listenPort = 8080;
        String backendHost = "127.0.0.1";
        int backendPort = 25565;

        ServerSocket server = new ServerSocket(listenPort);
        System.out.println("WebSocket proxy running on port " + listenPort);

        while (true) {
            Socket client = server.accept();
            Socket backend = new Socket(backendHost, backendPort);

            new Thread(() -> pipe(client, backend)).start();
            new Thread(() -> pipe(backend, client)).start();
        }
    }

    static void pipe(Socket in, Socket out) {
        try {
            InputStream is = in.getInputStream();
            OutputStream os = out.getOutputStream();
            byte[] buf = new byte[8192];
            int len;
            while ((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
                os.flush();
            }
        } catch (Exception e) {}
        try { in.close(); } catch (Exception e) {}
        try { out.close(); } catch (Exception e) {}
    }
}
