package handlers;

import com.google.gson.Gson;
import spark.Request;

public class JsonHandler {
    private final Gson gson;

    public JsonHandler() {
        this.gson = new Gson();
    }

    public <T> T fromJson(Request req, Class<T> classOfT) {
        return gson.fromJson(req.body(), classOfT);
    }

    public String toJson(Object res) {
        return gson.toJson(res);
    }
}
