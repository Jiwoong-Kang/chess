package dataAccess;

import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {

    HashSet<GameData> games;

    public MemoryGameDAO() {
        games = HashSet.newHashSet(16);
    }

    @Override
    public HashSet<GameData> listGames(){
        return games;
    }

    @Override
    public void createGame(GameData game){
        games.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game not found, id: " +gameID);
    }

    @Override
    public boolean gameExists(int gameID) {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateGame(GameData game){
        try{
            games.remove(getGame(game.gameID()));
            games.add(game);
        }catch(DataAccessException e){
            games.add(game);
        }
    }
    @Override
    public void clear() {
        games = HashSet.newHashSet(16);
    }
}
