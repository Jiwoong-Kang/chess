package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.sql.SqlGameDAO;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {

    private GameDAO dao;
    private GameData defaultGameData;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        dao = new SqlGameDAO();
        truncateGameTable();
        ChessGame defaultChessGame = new ChessGame();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        defaultChessGame.setBoard(board);
        defaultGameData = new GameData
                (1234, "white", "black", "gamename", defaultChessGame);
    }

    @AfterEach
    void tearDown() throws SQLException, DataAccessException {
        truncateGameTable();
    }

    private void truncateGameTable() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("TRUNCATE game")) {
            statement.executeUpdate();
        }
    }

    private GameData getGameDataFromDatabase(int gameID) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement
                     ("SELECT gameID, whiteUsername, blackUsername, " +
                             "gameName, chessGame FROM game WHERE gameID=?")) {
            statement.setInt(1, gameID);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return new GameData(
                            results.getInt("gameID"),
                            results.getString("whiteUsername"),
                            results.getString("blackUsername"),
                            results.getString("gameName"),
                            deserializeGame(results.getString("chessGame"))
                    );
                }
                return null;
            }
        }
    }

    private int getGameCount() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM game");
             ResultSet results = statement.executeQuery()) {
            if (results.next()) {
                return results.getInt(1);
            }
            return 0;
        }
    }

    @Test
    void createGamePositive() throws DataAccessException, SQLException {
        dao.createGame(defaultGameData);
        GameData resultGameData = getGameDataFromDatabase(defaultGameData.gameID());
        assertNotNull(resultGameData, "Game data should not be null");
        assertEquals(defaultGameData, resultGameData);
    }

    @Test
    void createGameNegative() throws DataAccessException {
        dao.createGame(defaultGameData);
        assertThrows(DataAccessException.class, () -> dao.createGame(defaultGameData));
    }

    @Test
    void listGamesPositive() throws DataAccessException, SQLException {
        dao.createGame(defaultGameData);
        dao.createGame(new GameData
                (2345, "white", "black", "gamename", new ChessGame()));
        HashSet<GameData> resultGames = dao.listGames();
        assertEquals(getGameCount(), resultGames.size(), "Improper game count in list");
    }

    @Test
    void listGamesNegative() {
        HashSet<GameData> games = dao.listGames();
        assertEquals(0, games.size(), "Expected an empty set from listGames()");
    }

    @Test
    void getGamePositive() throws DataAccessException {
        dao.createGame(defaultGameData);
        assertEquals(defaultGameData, dao.getGame(defaultGameData.gameID()));
    }

    @Test
    void getGameNegative() {
        assertThrows(DataAccessException.class, () -> dao.getGame(defaultGameData.gameID()));
    }

    @Test
    void gameExistsPositive() throws DataAccessException {
        dao.createGame(defaultGameData);
        assertTrue(dao.gameExists(defaultGameData.gameID()));
    }

    @Test
    void gameExistsNegative() {
        assertFalse(dao.gameExists(defaultGameData.gameID()));
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        dao.createGame(defaultGameData);
        GameData updatedGame = new GameData
                (defaultGameData.gameID(), "newWhite", "black",
                        "gamename", defaultGameData.game());
        dao.updateGame(updatedGame);
        assertEquals(updatedGame, dao.getGame(defaultGameData.gameID()));
    }

    @Test
    void updateGameNegative() {
        assertThrows(DataAccessException.class, () -> dao.updateGame(defaultGameData));
    }

    @Test
    void clear() throws DataAccessException, SQLException {
        dao.createGame(defaultGameData);
        dao.clear();
        assertNull(getGameDataFromDatabase(defaultGameData.gameID()), "Game should not exist after clear");
    }

    private ChessGame deserializeGame(String serializedGame) {
        return new Gson().fromJson(serializedGame, ChessGame.class);
    }
}