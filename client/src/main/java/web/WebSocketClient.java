package web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.*;
import com.google.gson.Gson;
import chess.ChessMove;
import ui.Data;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

public class WebSocketClient implements MessageHandler.Whole<String> {
    private final WebSocketObserver observer;
    private final Session session;
    private final Gson jsonSerializer = new Gson();

    public WebSocketClient(WebSocketObserver ui, String host, int port)
            throws URISyntaxException, DeploymentException, IOException {
        this.observer = ui;
        this.session = establishWebSocketConnection(host, port);
    }

    private Session establishWebSocketConnection(String host, int port)
            throws URISyntaxException, DeploymentException, IOException {
        URI serverUri = new URI(String.format("ws://%s:%d/ws", host, port));
        Session newSession = ContainerProvider.getWebSocketContainer().connectToServer(
                new Endpoint() {
                    @Override
                    public void onOpen(Session session, EndpointConfig endpointConfig) {}
                },
                serverUri
        );
        newSession.addMessageHandler(this);
        return newSession;
    }

    @Override
    public void onMessage(String message) {
        observer.receiveMessage(message);
    }

    private void sendCommand(Object command) throws IOException {
        String serializedCommand = jsonSerializer.toJson(command);
        session.getBasicRemote().sendText(serializedCommand);
    }

    public void connect() throws IOException {
        UserGameCommand connectCommand = new UserGameCommand(
                CommandType.CONNECT,
                Data.getInstance().getAuthToken(),
                Data.getInstance().getGameID()
        );
        sendCommand(connectCommand);
    }

    public void move(ChessMove move) throws IOException {
        MakeMove moveCommand = new MakeMove(
                Data.getInstance().getAuthToken(),
                Data.getInstance().getGameID(),
                move
        );
        sendCommand(moveCommand);
    }

    public void leave() throws IOException {
        UserGameCommand leaveCommand = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                Data.getInstance().getAuthToken(),
                Data.getInstance().getGameID()
        );
        sendCommand(leaveCommand);
    }

    public void resign() throws IOException {
        UserGameCommand resignCommand = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                Data.getInstance().getAuthToken(),
                Data.getInstance().getGameID()
        );
        sendCommand(resignCommand);
    }
}