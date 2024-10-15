package dataAccess;

import chess.ChessGame;

import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {

    HashSet<GameData> games;

    public MemoryGameDAO() {
        games = HashSet.newHashSet(16);
    }

    @Override
    public HashSet<GameData> listGames(String username) {
        HashSet<GameData> games = HashSet.newHashSet(16);
        for (GameData game : this.games) {
            if (game.whiteUsername().equals(username) ||
                    game.blackUsername().equals(username)) {
                games.add(game);
            }
        }
        return games;
    }

    @Override
    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
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
    public void clear() {
        games = HashSet.newHashSet(16);
    }
}
