import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class handler {

    public static void handleClient(Socket clientSocket) {
        try (clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = clientSocket.getOutputStream()) {
            String requestLine = in.readLine();
            if (requestLine == null) return;
            System.out.println("Request: " + requestLine);
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) return;

            SimpleHttpServer.HttpMethod method = SimpleHttpServer.HttpMethod.valueOf(parts[0]);
            String path = parts[1];

            httpResponse response = new httpResponse(out);
            if (method == SimpleHttpServer.HttpMethod.OPTIONS) {
                response.sendOptions("GET, POST, PUT, DELETE, OPTIONS");
            } else {
                handler.HttpHandler handler = SimpleHttpServer.routes.getOrDefault(path, controller::handle404);
                handler.handle(new httpRequest(in, method), response);
            }

            byte[] responseData = response.getResponse();
            out.write(responseData);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public interface HttpHandler {
        void handle(httpRequest req, httpResponse res) throws IOException;
    }
}