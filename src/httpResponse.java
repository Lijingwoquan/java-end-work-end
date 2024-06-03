import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.Writer;

public class httpResponse {
        private final PrintWriter out;

        httpResponse(Writer out) {
            this.out = new PrintWriter(out, true);
        }

        public void sendStatus(SimpleHttpServer.HttpStatus status) {
            out.printf("HTTP/1.1 %d %s%n", status.code, status.message);
            out.printf("Server: %s%n%n", SimpleHttpServer.SERVER_NAME);
        }

        public void sendOptions(String allowedMethods) {
            out.println("HTTP/1.1 200 OK");
            out.printf("Allow: %s%n", allowedMethods);
            out.println("Access-Control-Allow-Origin: *");
            out.printf("Access-Control-Allow-Methods: %s%n", allowedMethods);
            out.println("Access-Control-Allow-Headers: Content-Type");
            out.println();
        }

        public void sendJSON(SimpleHttpServer.HttpStatus status, JSONObject json) {
            String body = json.toString();
            out.printf("HTTP/1.1 %d %s\r\n", status.code, status.message);
            out.printf("Server: %s\r\n", SimpleHttpServer.SERVER_NAME);
            out.printf("Content-Type: application/json\r\n");
            out.printf("Content-Length: %d\r\n", body.length());
            out.printf("Connection: close\r\n");
            out.printf("\r\n");
            out.print(body);
            out.flush();
        }

}
