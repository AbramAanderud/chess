package server;

import dataaccess.DataAccessException;
import handlers.*;
import spark.Spark;
import  dataaccess.DatabaseManager

public class Server {
    DatabaseManager databaseManager = new DatabaseManager();
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
