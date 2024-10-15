package dataAccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {

    private HashSet<UserData> userCollection;
    MemoryUserDAO() {
        userCollection = HashSet.newHashSet(16);
    }

    @Override
    public UserData getUser(String username) {
        for (UserData user : userCollection) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }


    @Override
    public void createUser(String username, String password, String email) {
        userCollection.add(new UserData(username, password, email));
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        for (UserData user : userCollection) {
            if (user.username().equals(username) &&
                    user.password().equals(password)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        userCollection = HashSet.newHashSet(16);
    }
}