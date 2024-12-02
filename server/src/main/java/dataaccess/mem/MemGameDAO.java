package dataaccess.mem;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import dataaccess.DataAccessException;

import dataaccess.GameDAO;
import model.GameData;

public class MemGameDAO implements GameDAO {
    private final Map<Integer, GameData> gameStorage = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @Override
    public GameData addGame(GameData gameData) throws DataAccessException {
        validateGameData(gameData);
        int newId = idGenerator.incrementAndGet();
        GameData newGame = createGameWithId(newId, gameData);
        gameStorage.put(newId, newGame);
        return newGame;
    }

    @Override
    public void clear() throws DataAccessException {
        gameStorage.clear();
        idGenerator.set(0);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameStorage.get(gameID);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        validateGameUpdate(gameData);
        gameStorage.put(gameData.gameID(), gameData);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return Collections.unmodifiableCollection(gameStorage.values());
    }

    private void validateGameData(GameData gameData) throws DataAccessException {
        if (gameData.game() == null) {
            throw new DataAccessException("Cannot add a null game to the database");
        }
    }

    private GameData createGameWithId(int id, GameData gameData) {
        return new GameData(id, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(),
                gameData.game());
    }

    private void validateGameUpdate(GameData gameData) throws DataAccessException {
        if (!gameStorage.containsKey(gameData.gameID())) {
            throw new DataAccessException("Game does not exist (so it can't be updated)");
        }
        if (gameData.game() == null) {
            throw new DataAccessException("Cannot update a null game");
        }
    }
}