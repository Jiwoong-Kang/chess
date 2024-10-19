package dataAccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {

    private final HashSet<UserData> userCollection;

    public MemoryUserDAO() {
        this.userCollection = new HashSet<>(16);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : userCollection) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("User not found: " + username);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (userExists(user.username())) {
            throw new DataAccessException("User already exists: " + user.username());
        }
        userCollection.add(user);
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        return user.password().equals(password);
    }

    @Override
    public void clear() {
        userCollection.clear();
    }

    // Additional utility methods

    private boolean userExists(String username) {
        return userCollection.stream().anyMatch(user -> user.username().equals(username));
    }

    public int getUserCount() {
        return userCollection.size();
    }

    public boolean isEmpty() {
        return userCollection.isEmpty();
    }
}