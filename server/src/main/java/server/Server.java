package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import handlers.*;
import spark.Spark;


public class Server {

    public int run(int desiredPort) {
        try {
            DatabaseManager.initializeDB();
        } catch (DataAccessException e) {
            System.err.println("couldnt make database" + e.getMessage());
        }


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

        Spark.post("/game", (request, response) ->
                (new CreateGameHandler()).handleRequest(request, response));

        Spark.post("/session", (request, response) ->
                (new LoginHandler()).handleRequest(request, response));

        Spark.delete("/db", (request, response) ->
                (new ClearHandler()).handleRequest(response));

        Spark.delete("/session", (request, response) ->
                (new LogoutHandler()).handleRequest(request, response));

        Spark.get("/game", (request, response) ->
                (new ListHandler()).handleRequest(request, response));

        Spark.put("/game", (request, response) ->
                (new JoinGameHandler()).handleRequest(request, response));
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
