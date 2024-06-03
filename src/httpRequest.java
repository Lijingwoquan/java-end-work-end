import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class httpRequest {
    public SimpleHttpServer.HttpMethod method;
    private  BufferedReader in ;

    public  httpRequest(BufferedReader in, SimpleHttpServer.HttpMethod method) {
        this.in = in;
        this.method = method;
    }
    public JSONObject readJSON() throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        int contentLength = 0;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            in.read(buffer, 0, contentLength);
            body.append(buffer);
        }
        System.out.println("Received JSON: " + body); // 调试用
        return new JSONObject(body.toString());
    }
}