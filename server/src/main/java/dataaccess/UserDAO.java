package dataaccess;

import model.LoginRequest;
import model.UserData;

public interface UserDAO {

    void addUser(UserData user) throws DataAccessException;

    void clear() throws DataAccessException;

    boolean userExists(String username) throws DataAccessException;

    boolean validLogin(LoginRequest login) throws DataAccessException;

}