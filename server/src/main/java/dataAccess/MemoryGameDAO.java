package dataAccess;

import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {

    private final HashSet<GameData> games;

    public MemoryGameDAO() {
        this.games = new HashSet<>(16);
    }

    @Override
    public HashSet<GameData> listGames() {
        return new HashSet<>(games);
    }

    @Override
    public void createGame(GameData game) {
        games.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game not found, id: " + gameID);
    }

    @Override
    public boolean gameExists(int gameID) {
        return games.stream().anyMatch(game -> game.gameID() == gameID);
    }

    @Override
    public void updateGame(GameData game) {
        games.removeIf(g -> g.gameID() == game.gameID());
        games.add(game);
    }

    @Override
    public void clear() {
        games.clear();
    }

    // Additional utility methods

    public int getGameCount() {
        return games.size();
    }

    public boolean isEmpty() {
        return games.isEmpty();
    }
}