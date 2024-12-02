package dataaccess.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import com.google.gson.Gson;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import dataaccess.DataAccessException;

public class SqlGameDAO extends SqlDAO implements GameDAO {

    public SqlGameDAO() throws DataAccessException {
        super();
    }

    @Override
    protected String[] createQuery() {
        return new String[] { generateCreateTableQuery() };
    }

    private String generateCreateTableQuery() {
        return """
                CREATE TABLE IF NOT EXISTS `games` (
                    `gameID` INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    `gameName` VARCHAR(64) NOT NULL,
                    `whiteUsername` VARCHAR(64),
                    `blackUsername` VARCHAR(64),
                    `game` LONGTEXT NOT NULL
                )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
                """;
    }

    @Override
    public GameData addGame(GameData gameData) throws DataAccessException {
        String insertQuery = "INSERT INTO games (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";
        int generatedId = executeUpdate(insertQuery, gameData.gameName(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.game());
        return createGameDataWithId(gameData, generatedId);
    }

    private GameData createGameDataWithId(GameData gameData, int id) {
        return new GameData(id, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(),
                gameData.game());
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateQuery = "TRUNCATE TABLE games;";
        executeUpdate(truncateQuery);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String selectQuery = "SELECT * FROM games WHERE gameID = ?;";
        return executeQuery(selectQuery, this::extractSingleGame, gameID);
    }

    private GameData extractSingleGame(ResultSet resultSet) throws SQLException {
        return resultSet.next() ? parseGame(resultSet) : null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String selectAllQuery = "SELECT * FROM games;";
        return executeQuery(selectAllQuery, this::extractMultipleGames);
    }

    private Collection<GameData> extractMultipleGames(ResultSet resultSet) throws SQLException {
        Collection<GameData> games = new HashSet<>();
        while (resultSet.next()) {
            games.add(parseGame(resultSet));
        }
        return games;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String updateQuery = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?;";
        executeUpdate(updateQuery, gameData.whiteUsername(), gameData.blackUsername(), gameData.game(), gameData.gameID());
    }

    private static GameData parseGame(ResultSet resultSet) throws SQLException {
        return new GameData(
                resultSet.getInt("gameID"),
                resultSet.getString("whiteUsername"),
                resultSet.getString("blackUsername"),
                resultSet.getString("gameName"),
                deserializeChessGame(resultSet.getString("game"))
        );
    }

    private static ChessGame deserializeChessGame(String gameJson) {
        return new Gson().fromJson(gameJson, ChessGame.class);
    }

    private int executeUpdate(String sql, Object... params) throws DataAccessException {
        return update(sql, params);
    }

    private <T> T executeQuery(String sql, Parser<T> parser, Object... params) throws DataAccessException {
        return query(sql, parser, params);
    }
}