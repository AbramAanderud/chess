package websocketfacade;

import chess.ChessMove;
import com.google.gson.Gson;
import serverfacade.ResponseException;
import websocket.WebSocketHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver serverMessageObserver;

    public WebSocketFacade(String url, ServerMessageObserver serverMessageObserver) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "ws");


            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println("Received WebSocket message: " + message);

                    Gson gson = new Gson();
                    ServerMessage baseMessage = gson.fromJson(message, ServerMessage.class);

                    switch (baseMessage.getServerMessageType()) {
                        case LOAD_GAME:
                            LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                            serverMessageObserver.notify(loadGameMessage);
                            break;

                        case NOTIFICATION:
                            NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                            serverMessageObserver.notify(notificationMessage);
                            break;

                        case ERROR:
                        default:
                            serverMessageObserver.notify(baseMessage);
                            break;
                    }
                }
            });
        } catch (DeploymentException | URISyntaxException | IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("WebSocket connection established with session: " + session);
    }

    public void connect(String authToken, Integer gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String authToken,  Integer gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken,  Integer gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
