package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public HashSet<GameData> listGames(String authToken) throws UnauthorizedException {
        validateAuth(authToken);
        return gameDAO.listGames();
    }

    public int createGame(String authToken) throws UnauthorizedException {
        validateAuth(authToken);
        int gameID = generateUniqueGameID();
        gameDAO.createGame(new GameData(gameID, null, null, null, null));
        return gameID;
    }

    public boolean joinGame(String authToken, int gameID, String color) throws UnauthorizedException, BadRequestException {
        AuthData authData = validateAuth(authToken);
        GameData gameData = getGame(gameID);

        if (color == null) {
            return true; // Spectator join
        }

        switch (color.toUpperCase()) {
            case "WHITE":
                return joinAsWhite(authData, gameData);
            case "BLACK":
                return joinAsBlack(authData, gameData);
            default:
                throw new BadRequestException(String.format("%s is not a valid team color", color));
        }
    }

    public void clear() {
        gameDAO.clear();
    }

    private AuthData validateAuth(String authToken) throws UnauthorizedException {
        try {
            return authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    private GameData getGame(int gameID) throws BadRequestException {
        try {
            return gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private int generateUniqueGameID() {
        int gameID;
        do {
            gameID = ThreadLocalRandom.current().nextInt(1, 10000);
        } while (gameDAO.gameExists(gameID));
        return gameID;
    }

    private boolean joinAsWhite(AuthData authData, GameData gameData) {
        if (gameData.whiteUsername() != null) {
            return false;
        }
        gameDAO.updateGame(new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game()));
        return true;
    }

    private boolean joinAsBlack(AuthData authData, GameData gameData) {
        if (gameData.blackUsername() != null) {
            return false;
        }
        gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game()));
        return true;
    }
}