package dataaccess;

import dataaccess.sql.SqlAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class SQLAuthDAOTest {

    AuthDAO dao;
    AuthData defaultAuth;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        dao = new SqlAuthDAO();
        truncateAuthTable();
        defaultAuth = new AuthData("username", "token");
    }

    @AfterEach
    void tearDown() throws SQLException, DataAccessException {
        truncateAuthTable();
    }

    private void truncateAuthTable() throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("TRUNCATE auth")) {
            statement.executeUpdate();
        }
    }

    private boolean isAuthDataPresent(String username) throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT username, authToken FROM auth WHERE username=?")) {
            statement.setString(1, username);
            try (var results = statement.executeQuery()) {
                return results.next();
            }
        }
    }

    @Test
    void addAuthPositive() throws DataAccessException, SQLException {
        dao.addAuth(defaultAuth);
        String resultUsername;
        String resultToken;
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT username, authToken FROM auth WHERE username=?")) {
            statement.setString(1, defaultAuth.username());
            try (var results = statement.executeQuery()) {
                results.next();
                resultUsername = results.getString("username");
                resultToken = results.getString("authToken");
            }
        }
        assertEquals(defaultAuth, new AuthData(resultUsername, resultToken));
    }

    @Test
    void addAuthNegative() throws DataAccessException, SQLException {
        dao.addAuth(defaultAuth);
        dao.addAuth(defaultAuth);
        assertTrue(isAuthDataPresent(defaultAuth.username()));
        assertFalse(isAuthDataPresent(defaultAuth.username() + "nonexistent"));
    }

    @Test
    void deleteAuthPositive() throws DataAccessException, SQLException {
        dao.addAuth(defaultAuth);
        dao.deleteAuth(defaultAuth.authToken());
        assertFalse(isAuthDataPresent(defaultAuth.username()));
    }

    @Test
    void deleteAuthNegative() {
        assertDoesNotThrow(() -> dao.deleteAuth("badToken"));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        dao.addAuth(defaultAuth);
        AuthData result = dao.getAuth(defaultAuth.authToken());
        assertEquals(defaultAuth, result);
    }

    @Test
    void getAuthNegative() {
        dao.addAuth(defaultAuth);
        assertThrows(DataAccessException.class, () -> dao.getAuth("badToken"));
    }

    @Test
    void clear() throws DataAccessException, SQLException {
        dao.addAuth(defaultAuth);
        dao.clear();
        assertFalse(isAuthDataPresent(defaultAuth.username()));
    }
}