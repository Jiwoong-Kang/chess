package server;

import com.google.gson.Gson;
import dataaccess.UnauthorizedException;
import dataaccess.BadRequestException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import java.util.HashSet;

public class GameHandler {

    private final GameService gameService;
    private final Gson gson;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
        this.gson = new Gson();
    }

    public String listGames(Request req, Response resp) throws UnauthorizedException {
        String authToken = getAuthToken(req);
        HashSet<GameData> games = gameService.listGames(authToken);
        resp.status(200);
        return gson.toJson(new GamesResponse(games));
    }

    public String createGame(Request req, Response resp) throws BadRequestException, UnauthorizedException {
        CreateGameRequest createRequest = gson.fromJson(req.body(), CreateGameRequest.class);
        if (createRequest.gameName == null || createRequest.gameName.isEmpty()) {
            throw new BadRequestException("No gameName provided");
        }

        String authToken = getAuthToken(req);
        int gameID = gameService.createGame(authToken);
        resp.status(200);
        return gson.toJson(new CreateGameResponse(gameID));
    }

    public String joinGame(Request req, Response resp) throws BadRequestException, UnauthorizedException {
        JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);
        if (joinRequest.gameID == 0) {
            throw new BadRequestException("No gameID provided");
        }
        if (joinRequest.playerColor == null
                || (!joinRequest.playerColor.equalsIgnoreCase("WHITE")
                && !joinRequest.playerColor.equalsIgnoreCase("BLACK"))) {
            throw new BadRequestException("Invalid player color");
        }

        String authToken = getAuthToken(req);
        boolean joinSuccess = gameService.joinGame(authToken, joinRequest.gameID, joinRequest.playerColor);

        if (!joinSuccess) {
            resp.status(403);
            return gson.toJson(new ErrorResponse("Error: already taken"));
        }

        resp.status(200);
        return "{}";
    }

    private String getAuthToken(Request req) {
        return req.headers("authorization");
    }

    private static class GamesResponse {
        final HashSet<GameData> games;
        GamesResponse(HashSet<GameData> games) { this.games = games; }
    }

    private static class CreateGameRequest {
        String gameName;
    }

    private static class CreateGameResponse {
        final int gameID;
        CreateGameResponse(int gameID) { this.gameID = gameID; }
    }

    private static class JoinGameRequest {
        String playerColor;
        int gameID;
    }

    private static class ErrorResponse {
        final String message;
        ErrorResponse(String message) { this.message = message; }
    }
}