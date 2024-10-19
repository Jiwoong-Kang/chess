package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData createUser(UserData userData) throws BadRequestException {
        try {
            userDAO.createUser(userData);
            return generateAuthData(userData.username());
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public AuthData loginUser(UserData userData) throws UnauthorizedException {
        try {
            if (userDAO.authenticateUser(userData.username(), userData.password())) {
                return generateAuthData(userData.username());
            } else {
                throw new UnauthorizedException();
            }
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public void logoutUser(String authToken) throws UnauthorizedException {
        validateAuthToken(authToken);
        authDAO.deleteAuth(authToken);
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }

    private AuthData generateAuthData(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(username, authToken);
        authDAO.addAuth(authData);
        return authData;
    }

    private void validateAuthToken(String authToken) throws UnauthorizedException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }
}