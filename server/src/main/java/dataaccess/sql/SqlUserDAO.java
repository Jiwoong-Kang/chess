package dataaccess.sql;

import org.mindrot.jbcrypt.BCrypt;

import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.LoginRequest;
import model.UserData;

public class SqlUserDAO extends SqlDAO implements UserDAO {

    public SqlUserDAO() throws DataAccessException {
        super();
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        if (isUsernameTaken(user.username())) {
            throw new DataAccessException("The user already exists");
        }
        String insertQuery = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
        String encryptedPassword = encryptPassword(user.password());
        executeUpdate(insertQuery, user.username(), encryptedPassword, user.email());
    }

    @Override
    public void clear() throws DataAccessException {
        String truncateQuery = "TRUNCATE TABLE users;";
        executeUpdate(truncateQuery);
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        String selectQuery = "SELECT username FROM users WHERE username = ?;";
        return executeQuery(selectQuery, resultSet -> resultSet.next(), username);
    }

    @Override
    public boolean validLogin(LoginRequest login) throws DataAccessException {
        String selectQuery = "SELECT username, password FROM users WHERE username = ?;";
        return executeQuery(selectQuery, resultSet -> {
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return verifyPassword(login.password(), storedPassword);
            }
            return false;
        }, login.username());
    }

    @Override
    protected String[] createQuery() {
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS `users` (
                    `username` varchar(64) NOT NULL PRIMARY KEY,
                    `password` varchar(64) NOT NULL,
                    `email` varchar(64) NOT NULL
                )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
                """;

        return new String[] { createTableQuery };
    }

    private boolean isUsernameTaken(String username) throws DataAccessException {
        return userExists(username);
    }

    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private boolean verifyPassword(String inputPassword, String storedPassword) {
        return BCrypt.checkpw(inputPassword, storedPassword);
    }

    private void executeUpdate(String sql, Object... params) throws DataAccessException {
        update(sql, params);
    }

    private <T> T executeQuery(String sql, Parser<T> parser, Object... params) throws DataAccessException {
        return query(sql, parser, params);
    }
}