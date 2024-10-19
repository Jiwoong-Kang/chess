package service;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {

    GameDAO gameDAO;
    AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public HashSet<GameData> listGames(String authToken) throws DataAccessException {
        authDAO.getAuth(authToken); // throws if not authorized
        return gameDAO.listGames();
    }

    public int createGame(String authToken) throws DataAccessException {
        authDAO.getAuth(authToken);

        int gameID;

        do {
            gameID = ThreadLocalRandom.current().nextInt(1, 10000);
        } while (gameDAO.gameExists(gameID));

        gameDAO.createGame(new GameData(gameID, null, null, null, null));

        return gameID;
    }

    public int joinGame(String authToken, int gameID, String color) throws UnauthorizedException, DataAccessException {
        AuthData authData;
        GameData gameData;

        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Invalid authToken");
        }

        if (gameDAO.gameExists(gameID)) {
            gameData = gameDAO.getGame(gameID);
        } else return 1;

        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();

        if (Objects.equals(color, "WHITE")) {
            if (whiteUser != null) return 2;
            else whiteUser = authData.username();
        } else if (Objects.equals(color, "BLACK")) {
            if (blackUser != null) return 2;
            else blackUser = authData.username();
        } else if (color != null) return 1;

        gameDAO.updateGame(new GameData(gameID, whiteUser, blackUser, gameData.gameName(), gameData.game()));
        return 0;
    }


    public void clear() {
        gameDAO.clear();
    }

}
