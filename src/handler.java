import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class handler {

    public interface HttpHandler {
        void handle(httpRequest req, httpResponse res) throws IOException;
    }

    public static void handleClient(Socket clientSocket) {
        try (clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8))) {
            String requestLine = in.readLine();
            if (requestLine == null) return;
            System.out.println("Request: " + requestLine);
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) return;

            SimpleHttpServer.HttpMethod method = SimpleHttpServer.HttpMethod.valueOf(parts[0]);
            String path = parts[1];

            if (method == SimpleHttpServer.HttpMethod.OPTIONS) {
                new httpResponse(out).sendOptions("GET, POST, PUT, DELETE, OPTIONS");
            } else {
                handler.HttpHandler handler = SimpleHttpServer.routes.getOrDefault(path, controller::handle404);
                handler.handle(new httpRequest(in, method), new httpResponse(out));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

