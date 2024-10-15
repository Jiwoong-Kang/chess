package service;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserAuthService {

    UserDAO userDAO;
    AuthDAO authDAO;

    public UserAuthService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData createUser(UserData userData) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);
        userDAO.createUser(userData.username(), userData.password(), userData.email()); // 아니면 그냥 userdata
        authDAO.addAuth(authData.username(), authData.authToken()); // 아니면 그냥 authData
        return authData;
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }

}