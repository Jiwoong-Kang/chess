package dataAccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {

    HashSet<AuthData> AuthData;

    public MemoryAuthDAO() {
        AuthData = HashSet.newHashSet(16);
    }

    @Override
    public void addAuth(String authToken, String username) {
        AuthData.add(new AuthData(username, authToken));
    }

    @Override
    public void deleteAuth(String authToken) {
        for (AuthData authData : AuthData) {
            if (authData.authToken().equals(authToken)) {
                AuthData.remove(authData);
                break;
            }
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData authData : AuthData) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        throw new DataAccessException("Auth Token does not exist: " + authToken);
    }

    @Override
    public void clear() {
        AuthData = HashSet.newHashSet(16);
    }

}