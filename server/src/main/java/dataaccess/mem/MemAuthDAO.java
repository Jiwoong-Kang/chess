package dataaccess.mem;

import java.util.HashMap;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MemAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> tokens = new HashMap<>();


    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        if (tokens.containsKey(authData.authToken())) {
            throw new DataAccessException("Error: Token already exists");
        }
        tokens.put(authData.authToken(), authData);
    }


    @Override
    public void clear() throws DataAccessException {
        tokens.clear();
    }


    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        tokens.remove(authToken);

    }


    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return tokens.get(authToken);
    }

}