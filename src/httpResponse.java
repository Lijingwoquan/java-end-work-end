import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class httpResponse {
    private final ByteArrayOutputStream baos;

    httpResponse() {
        this.baos = new ByteArrayOutputStream();
    }

    public void sendStatus(SimpleHttpServer.HttpStatus status) throws IOException {
        String statusLine = "HTTP/1.1 " + status.code + " " + status.message + "\r\n";
        baos.write(statusLine.getBytes(StandardCharsets.UTF_8));
        baos.write(("Server: " + SimpleHttpServer.SERVER_NAME + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
    }

    public void sendOptions(String allowedMethods) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Allow: " + allowedMethods + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "Access-Control-Allow-Methods: " + allowedMethods + "\r\n" +
                "Access-Control-Allow-Headers: Content-Type\r\n\r\n";
        baos.write(response.getBytes(StandardCharsets.UTF_8));
    }

    public void sendJSON(SimpleHttpServer.HttpStatus status, JSONObject json) throws IOException {
        byte[] body = json.toString().getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 " + status.code + " " + status.message + "\r\n" +
                "Server: " + SimpleHttpServer.SERVER_NAME + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + body.length + "\r\n" +
                "Connection: close\r\n\r\n";
        //写入请求头
        baos.write(headers.getBytes(StandardCharsets.UTF_8));
        //写入请求体
        baos.write(body);
    }

    public byte[] getResponse() {
        return baos.toByteArray();
    }
}