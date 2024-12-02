package dataaccess.mem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dataaccess.UserDAO;
import model.LoginRequest;
import model.UserData;
import dataaccess.DataAccessException;

public class MemUserDAO implements UserDAO {
    private final Map<String, UserData> userStorage = new ConcurrentHashMap<>();

    @Override
    public void addUser(UserData newUser) throws DataAccessException {
        if (isUserRegistered(newUser.username())) {
            throw new DataAccessException("The user already exists");
        }
        storeUser(newUser);
    }

    @Override
    public void clear() throws DataAccessException {
        clearAllUsers();
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        return isUserRegistered(username);
    }

    @Override
    public boolean validLogin(LoginRequest loginAttempt) throws DataAccessException {
        return verifyLoginCredentials(loginAttempt);
    }

    private boolean isUserRegistered(String username) {
        return userStorage.containsKey(username);
    }

    private void storeUser(UserData user) {
        userStorage.put(user.username(), user);
    }

    private void clearAllUsers() {
        userStorage.clear();
    }

    private boolean verifyLoginCredentials(LoginRequest login) {
        UserData storedUser = userStorage.get(login.username());
        return storedUser != null && isPasswordMatch(storedUser, login);
    }

    private boolean isPasswordMatch(UserData storedUser, LoginRequest login) {
        return storedUser.password().equals(login.password());
    }
}