package dataaccess.mem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import dataaccess.DataAccessException;

import dataaccess.GameDAO;
import model.GameData;

public class MemGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();


    @Override
    public GameData addGame(GameData gameData) throws DataAccessException {
        if (gameData.game() == null) {
            throw new DataAccessException("Cannot add a null game to the database");
        }
        int id = games.size() + 1;
        GameData game = new GameData(id, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(),
                gameData.game());
        games.put(id, game);
        return game;
    }


    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }


    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }


    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (!games.containsKey(gameData.gameID())) {
            throw new DataAccessException("Game does not exist (so it can't be updated)");
        }
        if (gameData.game() == null) {
            throw new DataAccessException("Cannot update a null game");
        }
        games.remove(gameData.gameID());
        games.put(gameData.gameID(), gameData);
    }


    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return Collections.unmodifiableCollection(games.values());
    }

}