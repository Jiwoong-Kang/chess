package dataaccess;

import java.util.Collection;

import model.GameData;

public interface GameDAO {


    GameData addGame(GameData gameData) throws DataAccessException;

    void clear() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData gameData) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

}