import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class controller {
    public  static void handle404(httpRequest req, httpResponse res) {
        JSONObject response = new JSONObject();
        response.put("msg", "404");
        try {
            res.sendStatus(SimpleHttpServer.HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            res.sendJSON(SimpleHttpServer.HttpStatus.OK, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getGoods(httpResponse res, mysql.ResponseObj responseObj) {
        List<Map<String, Object>> carGoodsList = responseObj.getCarGoodsList();
        int maxPage = responseObj.getMaxPage();
        JSONArray carGoodsArray = new JSONArray(carGoodsList);
        JSONObject response = new JSONObject();
        response.put("carGoodsList", carGoodsArray);
        response.put("maxPage", maxPage);
        try {
            res.sendJSON(SimpleHttpServer.HttpStatus.OK, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handleGetGoodsForUser(httpRequest req, httpResponse res) {
        if (req.method == SimpleHttpServer.HttpMethod.GET) {
            mysql.ResponseObj responseObj = mysql.getInitListDateForUser();
            getGoods(res, responseObj);
        } else {
            try {
                res.sendStatus(SimpleHttpServer.HttpStatus.METHOD_NOT_ALLOWED);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void handleGetGoodsForManager(httpRequest req, httpResponse res) {
        if (req.method == SimpleHttpServer.HttpMethod.GET) {
            mysql.ResponseObj responseObj = mysql.getInitListDateForManger();
            getGoods(res, responseObj);
        } else {
            try {
                res.sendStatus(SimpleHttpServer.HttpStatus.METHOD_NOT_ALLOWED);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void handleAddGoods(httpRequest req, httpResponse res) throws IOException {
        if (req.method == SimpleHttpServer.HttpMethod.OPTIONS) {
            res.sendOptions("POST, OPTIONS");
        } else if (req.method == SimpleHttpServer.HttpMethod.POST) {
            JSONObject request = req.readJSON();
            String name = request.getString("name");
            int price = request.getInt("price");
            String imgUrl = request.getString("imgUrl");
            request.put("price", price);
            request.put("name", name);
            request.put("imgUrl", imgUrl);
            mysql.addGoods(request);
            res.sendJSON(SimpleHttpServer.HttpStatus.OK, request);
        } else {
            res.sendStatus(SimpleHttpServer.HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    public static void handleUpdateGoods(httpRequest req, httpResponse res) throws IOException {
        if (req.method == SimpleHttpServer.HttpMethod.OPTIONS) {
            res.sendOptions("POST, OPTIONS");
        } else if (req.method == SimpleHttpServer.HttpMethod.POST) {
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
            mysql.updateGoods(response);
            res.sendJSON(SimpleHttpServer.HttpStatus.OK, response);
        } else {
            res.sendStatus(SimpleHttpServer.HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    public static void handleChangeGoodsStatus(httpRequest req, httpResponse res) throws IOException {
        if (req.method == SimpleHttpServer.HttpMethod.OPTIONS) {
            res.sendOptions("POST, OPTIONS");
        } else if (req.method == SimpleHttpServer.HttpMethod.PUT) {
            JSONObject request = req.readJSON();
            int id = request.getInt("id");
            int status = request.getInt("status");
            JSONObject response = new JSONObject();
            response.put("msg", "delete success");
            mysql.changeGoodsStatus(id,status);
            res.sendJSON(SimpleHttpServer.HttpStatus.OK, response);
        } else {
            res.sendStatus(SimpleHttpServer.HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    public static void handleBuysGoods(httpRequest req, httpResponse res) throws IOException {
        if (req.method == SimpleHttpServer.HttpMethod.OPTIONS) {
            res.sendOptions("POST, OPTIONS");
        } else if (req.method == SimpleHttpServer.HttpMethod.POST) {
            JSONObject request = req.readJSON();
            System.out.println(request);
            mysql.buyGoods(request);
            JSONObject response = new JSONObject();
            response.put("msg", "buy success");
            res.sendJSON(SimpleHttpServer.HttpStatus.OK, response);
        } else {
            res.sendStatus(SimpleHttpServer.HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

}
