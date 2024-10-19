package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import dataAccess.BadRequestException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import java.util.HashSet;

public class GameHandler {

    GameService gameService;
    public GameHandler(GameService gameService){
        this.gameService = gameService;
    }

    public Object listGames(Request req, Response resp) throws UnauthorizedException{
            String authToken = req.headers("authorization");
            HashSet<GameData> games = gameService.listGames(authToken);
            resp.status(200);
            return "{ \"games\": %s}".formatted(new Gson().toJson(games));
    }

    public Object createGame(Request req, Response resp) throws BadRequestException, UnauthorizedException {
        if (!req.body().contains("\"gameName\":")) {
            throw new BadRequestException("No gameName provided");
        }

        String authToken = req.headers("authorization");
        int gameID =  gameService.createGame(authToken);
        resp.status(200);
        return "{ \"gameID\": %d }".formatted(gameID);

    }

    public Object joinGame(Request req, Response resp) throws BadRequestException, UnauthorizedException{

        if (!req.body().contains("\"gameID\":")) {
            throw new BadRequestException("No gameID provided");
        }

        try {
            String authToken = req.headers("authorization");
            record JoinGameData(String playerColor, int gameID) {}
            JoinGameData joinData = new Gson().fromJson(req.body(), JoinGameData.class);
            int joinStatus =  gameService.joinGame(authToken, joinData.gameID(), joinData.playerColor());
            if (joinStatus == 0) {
                resp.status(200);
                return "{}";
            } else if (joinStatus == 1) {
                resp.status(400);
                return "{ \"message\": \"Error: bad request\" }";
            } else if (joinStatus == 2) {
                resp.status(403);
                return "{ \"message\": \"Error: already taken\" }";
            }
            resp.status(200);
            return "{}";
        } catch (DataAccessException e) {
            resp.status(400);
            return "{ \"message\": \"Error: bad request\" }";
        } catch (UnauthorizedException e) {
            resp.status(401);
            return "{ \"message\": \"Error: unauthorized\" }";
        } catch (Exception e) {
            resp.status(500);
            return "{ \"message\": \"Error: %s\" }".formatted(e.getMessage());
        }
    }
}
