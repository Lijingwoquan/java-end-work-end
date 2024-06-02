import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/shopCar";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "..Lzh20050807..";
    private static Connection connection;

    public static void init() {
        try {
            // 加载 MySQL JDBC 驱动程序
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 建立数据库连接
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            // 创建 SQL 语句
            String sql = "CREATE TABLE IF NOT EXISTS car_goods (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(24) NOT NULL, " +
                    "imgUrl VARCHAR(100) NOT NULL, " +
                    "price INT NOT NULL, " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_time TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP)";
            String sql1 = "CREATE TABLE IF NOT EXISTS sold_list (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(24) NOT NULL, " +
                    "total_price INT NOT NULL, " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_time TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP)";

            // 执行创建表的 SQL 语句
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
            PreparedStatement statement1 = connection.prepareStatement(sql1);
            statement1.executeUpdate();
            statement1.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResponseObj getInitListDate() {
        List<Map<String, Object>> carGoodsList = new ArrayList<>();
        ResponseObj responseObj = new ResponseObj();
        int maxPage = 1;

        // 查询 car_goods 表中的所有数据
        String sql = "SELECT * FROM car_goods ORDER BY id DESC";

        try {
            PreparedStatement queryStatement = connection.prepareStatement(sql);
            ResultSet resultSet = queryStatement.executeQuery();
            int pageSize = 10; // 每页显示的记录数
            int currentPage = 1; // 当前页码
            int itemCount = 0; // 当前页的记录数
            while (resultSet.next()) {
                itemCount++;
                Map<String, Object> carGoods = new HashMap<>();
                carGoods.put("id", resultSet.getInt("id"));
                carGoods.put("name", resultSet.getString("name"));
                carGoods.put("price", resultSet.getInt("price"));
                carGoods.put("imgUrl", resultSet.getString("imgUrl"));
                carGoods.put("page", currentPage);
                // 将每一行数据添加到列表中
                carGoodsList.add(carGoods);
                // 如果当前页的记录数达到每页显示的记录数，则进入下一页
                if (itemCount == pageSize) {
                    currentPage++;
                    itemCount = 0;
                }
            }
            // 计算最大页码 根据carGoodsList长度计算 不能由currentPage得到
            // Math.ceil() 方法用于向上取整
            maxPage = (int) Math.ceil((double) carGoodsList.size() / pageSize);

            // 关闭结果集和语句
            resultSet.close();
            queryStatement.close();

            // 将查询到的数据和最大页码设置到响应对象中
            responseObj.setCarGoodsList(carGoodsList);
            responseObj.setMaxPage(maxPage);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return responseObj;
    }

    public static void addGoods(JSONObject object) {
        try {
            String name = object.get("name").toString();
            String imgUrl = object.get("imgUrl").toString();
            Integer price = (Integer) object.get("price");
            // 创建插入 SQL 语句
            String insertSQL = "INSERT INTO car_goods (name, price, imgUrl) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, price);
            preparedStatement.setString(3, imgUrl);
            // 执行插入操作
            preparedStatement.executeUpdate();
            // 关闭语句
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateGoods(JSONObject object) {
        try {
            int id = object.getInt("id");
            String name = object.get("name").toString();
            String imgUrl = object.get("imgUrl").toString();
            int price = object.getInt("price");
            // 创建插入 SQL 语句
            String updateSQL = "UPDATE car_goods SET name = ?, price = ?, imgUrl = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, price);
            preparedStatement.setString(3, imgUrl);
            preparedStatement.setInt(4, id);
            // 执行插入操作
            preparedStatement.executeUpdate();
            // 关闭语句
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteGoods(int id) {
        try {
            // 创建删除 SQL 语句
            String sql = "DELETE FROM car_goods WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // 设置参数
            preparedStatement.setInt(1, id);
            // 执行删除操作
            preparedStatement.executeUpdate();
            // 关闭语句
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 定义一个表示响应对象的类
    public static class ResponseObj {
        private List<Map<String, Object>> carGoodsList;
        private int maxPage;

        // getter 和 setter 方法
        public List<Map<String, Object>> getCarGoodsList() {
            return carGoodsList;
        }

        public void setCarGoodsList(List<Map<String, Object>> carGoodsList) {
            this.carGoodsList = carGoodsList;
        }

        public int getMaxPage() {
            return maxPage;
        }

        public void setMaxPage(int maxPage) {
            this.maxPage = maxPage;
        }
    }

}


class SimpleHttpServer {
    public static final int PORT = 8080;
    public static final int THREAD_POOL_SIZE = 10;
    public static final String SERVER_NAME = "SimpleHttpServer/1.0";
    public static final Map<String, HttpHandler> routes = new HashMap<>();
    public static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) {
        Database.init();
        routes.put("/api/getGoods", SimpleHttpServer::handleGetGoods);
        routes.put("/api/manager/addGoods", SimpleHttpServer::handleAddGoods);
        routes.put("/api/manager/updateGoods", SimpleHttpServer::handleUpdateGoods);
        routes.put("/api/manager/deleteGoods", SimpleHttpServer::handleDeleteGoods);
        routes.put("/api/user/buysGoods", SimpleHttpServer::handleBuysGoods);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

    public static void handle404(Request req, Response res) {
        JSONObject response = new JSONObject();
        response.put("msg", "404");
        res.sendStatus(HttpStatus.NOT_FOUND);
        res.sendJSON(HttpStatus.OK, response);
    }

    public static void handleGetGoods(Request req, Response res) {
        if (req.method == HttpMethod.GET) {
            Database.ResponseObj responseObj = Database.getInitListDate();
            List<Map<String, Object>> carGoodsList = responseObj.getCarGoodsList();
            int maxPage = responseObj.getMaxPage();
            JSONArray carGoodsArray = new JSONArray(carGoodsList);
            JSONObject response = new JSONObject();
            response.put("carGoodsList", carGoodsArray);
            response.put("maxPage", maxPage);
            res.sendJSON(HttpStatus.OK, response);
        } else {
            res.sendStatus(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    public static void handleAddGoods(Request req, Response res) throws IOException {
        if (req.method == HttpMethod.OPTIONS) {
            res.sendOptions("POST, OPTIONS");
        } else if (req.method == HttpMethod.POST) {
            JSONObject request = req.readJSON();
            String name = request.getString("name");
            int price = request.getInt("price");
            String imgUrl = request.getString("imgUrl");
            request.put("price", price);
            request.put("name", name);
            request.put("imgUrl", imgUrl);
            Database.addGoods(request);
            res.sendJSON(HttpStatus.OK, request);
        } else {
            res.sendStatus(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    public static void handleUpdateGoods(Request req, Response res) throws IOException {
        if (req.method == HttpMethod.OPTIONS) {
            res.sendOptions("POST, OPTIONS");
        } else if (req.method == HttpMethod.POST) {
            JSONObject request = req.readJSON();
            String name = request.getString("name");
            int price = request.getInt("price");
            String imgUrl = request.getString("imgUrl");
            int id = request.getInt("id");
            JSONObject response = new JSONObject();
            response.put("id", id);
            response.put("name", name);
            response.put("price", price);
            response.put("imgUrl", imgUrl);
            Database.updateGoods(response);
            res.sendJSON(HttpStatus.OK, response);
        } else {
            res.sendStatus(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    public static void handleDeleteGoods(Request req, Response res) throws IOException {
        if (req.method == HttpMethod.OPTIONS) {
            res.sendOptions("POST, OPTIONS");
        } else if (req.method == HttpMethod.DELETE) {
            JSONObject request = req.readJSON();
            int id = request.getInt("id");
            JSONObject response = new JSONObject();
            response.put("msg", "delete success");
            Database.deleteGoods(id);
            res.sendJSON(HttpStatus.OK, response);
        } else {
            res.sendStatus(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    public static void handleBuysGoods(Request req, Response res) throws IOException {
        if (req.method == HttpMethod.OPTIONS) {
            res.sendOptions("POST, OPTIONS");
        } else if (req.method == HttpMethod.POST) {
            JSONObject request = req.readJSON();

//            res.sendJSON(SimpleHttpServer.HttpStatus.OK, response);
        } else {
            res.sendStatus(HttpStatus.METHOD_NOT_ALLOWED);
        }
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

            HttpMethod method = HttpMethod.valueOf(parts[0]);
            String path = parts[1];

            if (method == HttpMethod.OPTIONS) {
                new Response(out).sendOptions("GET, POST, PUT, DELETE, OPTIONS");
            } else {
                HttpHandler handler = routes.getOrDefault(path, SimpleHttpServer::handle404);
                handler.handle(new Request(in, method), new Response(out));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public interface HttpHandler {
        void handle(Request req, Response res) throws IOException;
    }

    public static class Request {
        public final HttpMethod method;
        private final BufferedReader in;

        Request(BufferedReader in, HttpMethod method) {
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

    public static class Response {
        private final PrintWriter out;

        Response(Writer out) {
            this.out = new PrintWriter(out, true);
        }

        public void sendStatus(HttpStatus status) {
            out.printf("HTTP/1.1 %d %s%n", status.code, status.message);
            out.printf("Server: %s%n%n", SERVER_NAME);
        }

        public void sendOptions(String allowedMethods) {
            out.println("HTTP/1.1 200 OK");
            out.printf("Allow: %s%n", allowedMethods);
            out.println("Access-Control-Allow-Origin: *");
            out.printf("Access-Control-Allow-Methods: %s%n", allowedMethods);
            out.println("Access-Control-Allow-Headers: Content-Type");
            out.println();
        }

        public void sendJSON(HttpStatus status, JSONObject json) {
            String body = json.toString();
            out.printf("HTTP/1.1 %d %s\r\n", status.code, status.message);
            out.printf("Server: %s\r\n", SERVER_NAME);
            out.printf("Content-Type: application/json\r\n");
            out.printf("Content-Length: %d\r\n", body.length());
            out.printf("Connection: close\r\n");
            out.printf("\r\n");
            out.print(body);
            out.flush();
        }
    }
}