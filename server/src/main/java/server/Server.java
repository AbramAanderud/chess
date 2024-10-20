package server;

import handlers.ClearHandler;
import handlers.LoginHandler;
import handlers.RegisterHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //Clear
        //Register
        //Login
        //Logout
        //ListGames
        //CreateGame
        //JoinGame


        //for register
        Spark.post("/user", (request, response) ->
                (new RegisterHandler()).handleRequest(request, response));

        Spark.post("/session", (request, response) ->
                (new LoginHandler()).handleRequest(request, response));

        Spark.delete("/db", (request, response) ->
                (new ClearHandler()).handleRequest(request, response));


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
