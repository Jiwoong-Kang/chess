package dataAccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {

    private HashSet<UserData> userCollection;

    public MemoryUserDAO() {
        userCollection = HashSet.newHashSet(16);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        for (UserData user : userCollection) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("User not found: " + username);
    }


    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        try{
            getUser(username);
        }
        catch(DataAccessException e){
            userCollection.add(new UserData(username, password, email));
            return;
        }
        throw new DataAccessException("User already exists: " + username);
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        boolean userExists = false;
        for (UserData user : userCollection) {
            if(user.username().equals(username)){
                userExists = true;
            }
            if (user.username().equals(username) &&
                    user.password().equals(password)) {
                return true;
            }
        }
        if (userExists){
            return false;
        }
        else{
            throw new DataAccessException("User does not exist: " + username);
        }
    }

    @Override
    public void clear() {
        userCollection = HashSet.newHashSet(16);
    }
}