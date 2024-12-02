package dataaccess.sql;

import java.sql.*;
import dataaccess.DataAccessException;
import com.google.gson.Gson;
import chess.ChessGame;
import dataaccess.DatabaseManager;

public abstract class SqlDAO extends DatabaseManager {

    protected SqlDAO() throws DataAccessException {
        initializeDatabase();
    }

    private void initializeDatabase() throws DataAccessException {
        createDatabase();
        executeInitialQueries();
    }

    private void executeInitialQueries() throws DataAccessException {
        try (Connection connection = getConnection()) {
            for (String query : createQuery()) {
                executeStatement(connection, query);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void executeStatement(Connection connection, String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(query);
        }
    }

    protected <T> T query(String sql, Parser<T> parser, Object... params) throws DataAccessException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setStatementParameters(preparedStatement, params);
            return executeQuery(preparedStatement, parser);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private <T> T executeQuery(PreparedStatement preparedStatement, Parser<T> parser) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return parser.parse(resultSet);
        }
    }

    protected int update(String sql, Object... params) throws DataAccessException {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(preparedStatement, params);
            preparedStatement.executeUpdate();
            return getGeneratedKey(preparedStatement);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private int getGeneratedKey(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            return 0;
        }
    }

    private void setStatementParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof ChessGame) {
                statement.setString(i + 1, new Gson().toJson(params[i]));
            } else {
                statement.setObject(i + 1, params[i]);
            }
        }
    }

    @FunctionalInterface
    protected interface Parser<T> {
        T parse(ResultSet resultSet) throws SQLException;
    }

    protected abstract String[] createQuery();
}