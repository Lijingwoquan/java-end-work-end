import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mysql {

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
                    "status TINYINT  DEFAULT 1," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_time TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP)";
            String sql1 = "CREATE TABLE IF NOT EXISTS sold_list (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(24) NOT NULL, " +
                    "imgUrl VARCHAR(100) NOT NULL, " +
                    "price INT NOT NULL, " +
                    "sold_count INT DEFAULT 0, " +
                    "total_price INT DEFAULT 0, " +
                    "status TINYINT DEFAULT 1," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_time TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP)";

            // 执行创建表的 SQL 语句
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
            PreparedStatement statement1 = connection.prepareStatement(sql1);
            statement1.executeUpdate();
            statement1.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResponseObj getInitListDateForUser() {
        List<Map<String, Object>> carGoodsList = new ArrayList<>();
        ResponseObj responseObj = new ResponseObj();
        int maxPage;

        // 查询 car_goods 表中的所有数据
        String sql = "SELECT * FROM car_goods WHERE status = 1 ORDER BY id DESC ";

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

    public static ResponseObj getInitListDateForManger() {
        List<Map<String, Object>> carGoodsList = new ArrayList<>();
        ResponseObj responseObj = new ResponseObj();
        int maxPage;
        // 查询 car_goods 表中的所有数据
        String sql = "SELECT * FROM sold_list ORDER BY id DESC ";
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
                carGoods.put("imgUrl", resultSet.getString("imgUrl"));
                carGoods.put("count", resultSet.getString("sold_count"));
                carGoods.put("price", resultSet.getInt("price"));
                carGoods.put("status", resultSet.getInt("status"));

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
            String insertSQL2 = "INSERT INTO sold_list (name,price,imgUrl) VALUES (?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            PreparedStatement preparedStatement2 = connection.prepareStatement(insertSQL2);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, price);
            preparedStatement.setString(3, imgUrl);
            preparedStatement2.setString(1, name);
            preparedStatement2.setInt(2, price);
            preparedStatement2.setString(3, imgUrl);

            // 执行插入操作
            preparedStatement.executeUpdate();
            preparedStatement2.executeUpdate();
            // 关闭语句
            preparedStatement.close();
            preparedStatement2.close();
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
            String updateSQL2 = "UPDATE sold_list SET name = ?,price = ?, imgUrl=? WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
            PreparedStatement preparedStatement2 = connection.prepareStatement(updateSQL2);

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, price);
            preparedStatement.setString(3, imgUrl);
            preparedStatement.setInt(4, id);
            preparedStatement2.setString(1, name);
            preparedStatement2.setInt(2, price);
            preparedStatement2.setString(3, imgUrl);
            preparedStatement2.setInt(4, id);

            // 执行插入操作
            preparedStatement.executeUpdate();
            preparedStatement2.executeUpdate();

            // 关闭语句
            preparedStatement.close();
            preparedStatement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void changeGoodsStatus(int id,int status) {
        try {
            String changeStatusSQL = "UPDATE car_goods SET status = ? WHERE id = ?";
            String changeStatusSQL2 = "UPDATE sold_list SET status = ? WHERE id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(changeStatusSQL);
            PreparedStatement preparedStatement2 = connection.prepareStatement(changeStatusSQL2);

            preparedStatement.setInt(1, status);
            preparedStatement.setInt(2, id);

            preparedStatement2.setInt(1, status);
            preparedStatement2.setInt(2, id);


            preparedStatement.executeUpdate();
            preparedStatement2.executeUpdate();

            preparedStatement.close();
            preparedStatement2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void buyGoods(JSONObject goodsList) {
        JSONArray arr = goodsList.getJSONArray("goodsList");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject item = arr.getJSONObject(i);
            int id = item.getInt("id");
            int soldCount = item.getInt("count");
            int totalPrice = item.getInt("totalPrice");
            String sql = "UPDATE sold_list SET sold_count = sold_count + ?, total_price = total_price + ?  WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, soldCount);
                stmt.setInt(2, totalPrice);
                stmt.setInt(3, id);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 表示响应对象的类
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


