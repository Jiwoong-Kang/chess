package dataaccess.sql;

import dataaccess.AuthDAO;
import model.AuthData;
import dataaccess.DataAccessException;

public class SqlAuthDAO extends SqlDAO implements AuthDAO {

    public SqlAuthDAO() throws DataAccessException {
        super();
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        if (isAuthTokenExists(authData.authToken())) {
            throw new DataAccessException("Error: Auth token already exists");
        }
        String insertQuery = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(insertQuery, authData.authToken(), authData.username());
    }

    @Override
    public void clear() throws DataAccessException {
        String deleteQuery = "DELETE FROM auth";
        executeUpdate(deleteQuery);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!isAuthTokenExists(authToken)) {
            throw new DataAccessException("Error: Auth token does not exist, cannot delete");
        }
        String deleteQuery = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(deleteQuery, authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String selectQuery = "SELECT * FROM auth WHERE authToken = ?";
        return executeQuery(selectQuery, resultSet -> {
            if (resultSet.next()) {
                return new AuthData(resultSet.getString("authToken"), resultSet.getString("username"));
            }
            return null;
        }, authToken);
    }

    @Override
    protected String[] createQuery() {
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS `auth` (
                    `authToken` varchar(64) NOT NULL,
                    `username` varchar(64) NOT NULL
                )ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
                """;
        return new String[] { createTableQuery };
    }

    private boolean isAuthTokenExists(String authToken) throws DataAccessException {
        return getAuth(authToken) != null;
    }

    private void executeUpdate(String sql, Object... params) throws DataAccessException {
        update(sql, params);
    }

    private <T> T executeQuery(String sql, Parser<T> parser, Object... params) throws DataAccessException {
        return query(sql, parser, params);
    }
}