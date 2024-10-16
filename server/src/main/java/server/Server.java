package server;

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
