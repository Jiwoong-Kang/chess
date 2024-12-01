package dataaccess;

import dataaccess.sql.SqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {

    private UserDAO dao;
    private UserData defaultUser;

    @BeforeEach
    void setUp() throws DataAccessException, SQLException {
        DatabaseManager.createDatabase();
        dao = new SqlUserDAO();
        truncateUserTable();
        defaultUser = new UserData("username", "password", "email");
    }

    @AfterEach
    void tearDown() throws SQLException, DataAccessException {
        truncateUserTable();
    }

    private void truncateUserTable() throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement("TRUNCATE user")) {
            statement.executeUpdate();
        }
    }

    private UserData getUserFromDatabase(String username) throws SQLException, DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement
                     ("SELECT username, password, email FROM user WHERE username=?")) {
            statement.setString(1, username);
            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return new UserData(
                            results.getString("username"),
                            results.getString("password"),
                            results.getString("email")
                    );
                }
                return null;
            }
        }
    }

    @Test
    void createUserPositive() throws DataAccessException, SQLException {
        dao.createUser(defaultUser);
        UserData resultUser = getUserFromDatabase(defaultUser.username());
        assertNotNull(resultUser, "User should exist in database");
        assertEquals(defaultUser.username(), resultUser.username());
        assertTrue(passwordMatches(defaultUser.password(), resultUser.password()));
        assertEquals(defaultUser.email(), resultUser.email());
    }

    @Test
    void createUserNegative() throws DataAccessException {
        dao.createUser(defaultUser);
        assertThrows(DataAccessException.class, () -> dao.createUser(defaultUser));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        dao.createUser(defaultUser);
        UserData resultUser = dao.getUser(defaultUser.username());
        assertNotNull(resultUser, "User should be retrieved");
        assertEquals(defaultUser.username(), resultUser.username());
        assertTrue(passwordMatches(defaultUser.password(), resultUser.password()));
        assertEquals(defaultUser.email(), resultUser.email());
    }

    @Test
    void getUserNegative() {
        assertThrows(DataAccessException.class, () -> dao.getUser(defaultUser.username()));
    }

    @Test
    void authenticateUserPositive() throws DataAccessException {
        dao.createUser(defaultUser);
        assertTrue(dao.authenticateUser(defaultUser.username(), defaultUser.password()));
    }

    @Test
    void authenticateUserNegative() throws DataAccessException {
        dao.createUser(defaultUser);
        assertFalse(dao.authenticateUser(defaultUser.username(), "badPass"));
    }

    @Test
    void clear() throws DataAccessException, SQLException {
        dao.createUser(defaultUser);
        dao.clear();
        assertNull(getUserFromDatabase(defaultUser.username()), "User should not exist after clear");
    }

    private boolean passwordMatches(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }
}