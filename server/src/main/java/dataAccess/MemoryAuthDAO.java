package dataAccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {

    HashSet<AuthData> dateBase;

    public MemoryAuthDAO() {
        dateBase = HashSet.newHashSet(16);
    }

    @Override
    public void addAuth(AuthData authData) {
        dateBase.add(authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        for (AuthData authData : dateBase) {
            if (authData.authToken().equals(authToken)) {
                dateBase.remove(authData);
                break;
            }
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData authData : dateBase) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        throw new DataAccessException("Auth Token does not exist: " + authToken);
    }

    @Override
    public void clear() {
        dateBase = HashSet.newHashSet(16);
    }

}