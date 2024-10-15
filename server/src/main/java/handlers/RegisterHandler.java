package handlers;
import spark.*;

public class RegisterHandler {

    public String handleRequest(Request req, Response res) {
        JsonHandler jsonHandler = new JsonHandler();

        return jsonHandler.toJson(res);
    }
}
