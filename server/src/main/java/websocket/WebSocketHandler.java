package websocket;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.gson.Gson;

import chess.ChessBoard;
import chess.ChessGame.TeamColor;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

@WebSocket
public class WebSocketHandler {
    private static final WebSocketHandler INSTANCE = new WebSocketHandler();
    public static WebSocketHandler getInstance() { return INSTANCE; }

    private DataAccess dataAccess;
    private final ConnectionManager manager = new ConnectionManager();

    public void setDataAccess(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketConnect
    public void handleConnect(Session session) {
        manager.add(session, 0);
    }

    @OnWebSocketClose
    public void handleClose(Session session, int statusCode, String reason) {
        manager.remove(session);
    }

    @OnWebSocketMessage
    public void handleMessage(Session session, String message) throws IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        GameSession dataPair = retrieveData(session, cmd);
        if (dataPair == null) return;

        switch (cmd.getCommandType()) {
            case CONNECT -> handleConnect(session, cmd, dataPair);
            case MAKE_MOVE -> {
                MakeMove moveCmd = new Gson().fromJson(message, MakeMove.class);
                handleMove(session, moveCmd, dataPair);
            }
            case RESIGN -> handleResign(session, cmd, dataPair);
            case LEAVE -> handleLeave(session, cmd, dataPair);
        }
    }

    private GameSession retrieveData(Session session, UserGameCommand cmd) throws IOException {
        try {
            AuthData authData = dataAccess.getAuthDAO().getAuth(cmd.getAuthToken());
            if (authData == null) {
                sendError(session, "Error: Invalid token. Unauthorized request");
                return null;
            }
            GameData gameData = dataAccess.getGameDAO().getGame(cmd.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Game does not exist.");
                return null;
            }
            return new GameSession(authData, gameData);
        } catch (DataAccessException e) {
            sendError(session, "Error: Invalid Request");
            return null;
        }
    }

    private void sendError(Session session, String message) throws IOException {
        manager.error(session, message);
    }

    private void handleConnect(Session session, UserGameCommand cmd, GameSession dataPair) throws IOException {
        String username = dataPair.getAuthData().username();
        GameData gameData = dataPair.getGameData();
        manager.add(session, gameData.gameID());

        TeamColor joinColor = getTeamColor(username, gameData);
        Notification notification = (joinColor != null)
                ? new Notification("%s has joined the game as %s.".formatted(username, joinColor.toString().toLowerCase()))
                : new Notification("%s is now observing the game.".formatted(username));

        manager.broadcast(session, new Gson().toJson(notification));
        manager.send(session, new Gson().toJson(new LoadGame(gameData.game())));
    }

    private void handleMove(Session session, MakeMove cmd, GameSession dataPair) throws IOException {
        String username = dataPair.getAuthData().username();
        GameData gameData = dataPair.getGameData();
        TeamColor userColor = getTeamColor(username, gameData);
        ChessMove move = cmd.getMove();

        if (userColor == null) {
            sendError(session, "Error: You are not playing in this game.");
            return;
        }
        if (gameData.game().getGameOver()) {
            sendError(session, "Error: Game is over. No more moves can be made.");
            return;
        }

        TeamColor opponent = (userColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        if (gameData.game().getTeamTurn().equals(opponent)) {
            sendError(session, "Error: It is not your turn.");
            return;
        }

        ChessBoard board = gameData.game().getBoard();
        if (board.getPiece(move.getStartPosition()) == null) {
            sendError(session, "Error: You are trying to move a piece that does not exist.");
            return;
        }
        if (board.getPiece(move.getStartPosition()).getTeamColor().equals(opponent)) {
            sendError(session, "Error: You can only move your own pieces.");
            return;
        }

        try {
            gameData.game().makeMove(move);
            String start = chessNotation(move.getStartPosition());
            String end = chessNotation(move.getEndPosition());
            Notification notif = new Notification("%s has made a move from %s to %s".formatted(username, start, end));
            manager.broadcast(session, new Gson().toJson(notif));

            if (gameData.game().isInCheckmate(opponent)) {
                handleCheckmate(session, opponent, gameData);
            } else if (gameData.game().isInStalemate(opponent)) {
                handleStalemate(session, username, gameData);
            } else if (gameData.game().isInCheck(opponent)) {
                handleCheck(session, opponent);
            }

            dataAccess.getGameDAO().updateGame(gameData);
            broadcastGame(session, gameData);
            sendGame(session, gameData);
        } catch (InvalidMoveException e) {
            sendError(session, "That is not a valid move.");
        } catch (DataAccessException e) {
            sendError(session, "Error: Invalid Request");
        }
    }

    private void handleCheckmate(Session session, TeamColor opponent, GameData gameData) throws IOException {
        Notification notif = new Notification("Checkmate! %s is the winner.".formatted(opponent.toString().toLowerCase()));
        gameData.game().setGameOver(true);
        String notifJson = new Gson().toJson(notif);
        manager.send(session, notifJson);
        manager.broadcast(session, notifJson);
    }

    private void handleStalemate(Session session, String username, GameData gameData) throws IOException {
        Notification notif = new Notification("Stalemate caused by %s. Game ends with a tie!".formatted(username));
        gameData.game().setGameOver(true);
        String notifJson = new Gson().toJson(notif);
        manager.send(session, notifJson);
        manager.broadcast(session, notifJson);
    }

    private void handleCheck(Session session, TeamColor opponent) throws IOException {
        Notification notif = new Notification("%s is in check.".formatted(opponent.toString().toLowerCase()));
        String notifJson = new Gson().toJson(notif);
        manager.send(session, notifJson);
        manager.broadcast(session, notifJson);
    }

    private String chessNotation(ChessPosition pos) {
        int row = pos.getRow();
        int col = pos.getColumn();
        return Character.toString("abcdefgh".charAt(col - 1)) + row;
    }

    private void handleResign(Session session, UserGameCommand cmd, GameSession dataPair) throws IOException {
        String username = dataPair.getAuthData().username();
        GameData gameData = dataPair.getGameData();
        TeamColor userColor = getTeamColor(username, gameData);
        String opponentUsername = (userColor == TeamColor.WHITE) ? gameData.blackUsername() : gameData.whiteUsername();

        if (userColor == null) {
            sendError(session, "Error: You are not playing in this game.");
            return;
        }
        if (gameData.game().getGameOver()) {
            sendError(session, "Error: Game is over. No more moves can be made.");
            return;
        }

        gameData.game().setGameOver(true);
        try {
            dataAccess.getGameDAO().updateGame(gameData);
        } catch (DataAccessException e) {
            sendError(session, "Error: Invalid Request");
            return;
        }

        Notification notif = new Notification("%s has resigned, %s is the winner!".formatted(username, opponentUsername));
        manager.broadcast(session, new Gson().toJson(notif));
        manager.send(session, new Gson().toJson(notif));
    }

    private void handleLeave(Session session, UserGameCommand cmd, GameSession dataPair) throws IOException {
        String username = dataPair.getAuthData().username();
        TeamColor userColor = getTeamColor(username, dataPair.getGameData());
        GameData gameData = dataPair.getGameData();

        Notification notification = new Notification("%s has left the game.".formatted(username));
        manager.broadcast(session, new Gson().toJson(notification));

        if (userColor.equals(TeamColor.WHITE)) {
            gameData = gameData.setWhiteUsername(null);
        } else if (userColor.equals(TeamColor.BLACK)) {
            gameData = gameData.setBlackUsername(null);
        }

        try {
            dataAccess.getGameDAO().updateGame(gameData);
        } catch (DataAccessException e) {
            sendError(session, "Error: Invalid Request");
            return;
        }

        manager.remove(session);
    }

    private void broadcastGame(Session session, GameData gameData) throws IOException {
        LoadGame loadGame = new LoadGame(gameData.game());
        manager.broadcast(session, new Gson().toJson(loadGame));
    }

    private void sendGame(Session session, GameData gameData) throws IOException {
        LoadGame loadGame = new LoadGame(gameData.game());
        manager.send(session, new Gson().toJson(loadGame));
    }

    private TeamColor getTeamColor(String username, GameData gameData) {
        if (username.equals(gameData.whiteUsername())) return TeamColor.WHITE;
        if (username.equals(gameData.blackUsername())) return TeamColor.BLACK;
        return null;
    }
}