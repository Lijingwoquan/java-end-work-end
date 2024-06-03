import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SimpleHttpServer {
    public static final int PORT = 8080;
    public static final int THREAD_POOL_SIZE = 10;
    public static final String SERVER_NAME = "SimpleHttpServer/1.0";
    public static final Map<String, handler.HttpHandler> routes = new HashMap<>();
    public static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    public enum HttpMethod {GET, POST, PUT, DELETE, OPTIONS}
    public enum HttpStatus {
        OK(200, "OK"), NOT_FOUND(404, "Not Found"), METHOD_NOT_ALLOWED(405, "Method Not Allowed");
        public final int code;
        public final String message;

        HttpStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public static void main(String[] args) {
        mysql.init();
        routes.put("/api/user/getGoods", controller::handleGetGoodsForUser);
        routes.put("/api/manager/getGoods", controller::handleGetGoodsForManager);
        routes.put("/api/manager/addGoods", controller::handleAddGoods);
        routes.put("/api/manager/updateGoods", controller::handleUpdateGoods);
        routes.put("/api/manager/changeStatusGoods", controller::handleChangeGoodsStatus);
        routes.put("/api/user/buyGoods", controller::handleBuysGoods);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handler.handleClient(clientSocket));
                System.out.println("cnm");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }
}
