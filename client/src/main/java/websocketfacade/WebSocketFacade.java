package websocketfacade;

import chess.ChessMove;
import com.google.gson.Gson;
import serverfacade.ResponseException;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
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
    }

    public void connect(String authToken, Integer gameID, String playerColor, boolean isObserver) throws ResponseException {
        try {
            var connectCommand = new ConnectCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, isObserver, playerColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            var makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID, boolean isObserver) throws ResponseException {
        try {
            var leavecommand = new LeaveCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, isObserver);
            this.session.getBasicRemote().sendText(new Gson().toJson(leavecommand));

            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
