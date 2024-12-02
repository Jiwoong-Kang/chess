package dataaccess.mem;

import java.util.HashMap;

import dataaccess.UserDAO;
import model.LoginRequest;
import model.UserData;
import dataaccess.DataAccessException;

public class MemUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();


    @Override
    public void addUser(UserData user) throws DataAccessException {
        if (userExists(user.username())) {
            throw new DataAccessException("The user already exists");
        }
        users.put(user.username(), user);
    }


    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }


    @Override
    public boolean userExists(String username) throws DataAccessException {
        return users.containsKey(username);
    }


    @Override
    public boolean validLogin(LoginRequest login) throws DataAccessException {
        UserData dbUser = users.get(login.username());
        return dbUser != null && dbUser.password().equals(login.password());
    }

}