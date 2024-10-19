package dataAccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {

    private final HashSet<AuthData> database;

    public MemoryAuthDAO() {
        this.database = new HashSet<>(16);
    }

    @Override
    public void addAuth(AuthData authData) {
        database.add(authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        database.removeIf(authData -> authData.authToken().equals(authToken));
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData authData : database) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        throw new DataAccessException("Auth Token does not exist: " + authToken);
    }

    @Override
    public void clear() {
        database.clear();
    }

    // Additional utility methods

    public boolean containsAuthToken(String authToken) {
        for (AuthData authData : database) {
            if (authData.authToken().equals(authToken)) {
                return true;
            }
        }
        return false;
    }

    public int getAuthCount() {
        return database.size();
    }

    public boolean isEmpty() {
        return database.isEmpty();
    }
}