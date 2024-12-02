package service;

import java.security.SecureRandom;
import java.util.Base64;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginRequest;
import model.UserData;

public class UserService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder URL_SAFE_ENCODER = Base64.getUrlEncoder();
    private final DataAccess dataAccessor;

    public UserService(DataAccess dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    public AuthData register(UserData newUser) throws ServerException {
        try {
            validateUserData(newUser);
            checkUsernameAvailability(newUser.username());

            dataAccessor.getUserDAO().addUser(newUser);
            String generatedToken = createAuthToken();
            AuthData newAuthData = new AuthData(generatedToken, newUser.username());
            dataAccessor.getAuthDAO().addAuth(newAuthData);
            return newAuthData;
        } catch (DataAccessException ex) {
            throw new ServerException(ex);
        }
    }

    public AuthData login(LoginRequest credentials) throws ServerException {
        try {
            validateLoginCredentials(credentials);

            String generatedToken = createAuthToken();
            AuthData newAuthData = new AuthData(generatedToken, credentials.username());
            dataAccessor.getAuthDAO().addAuth(newAuthData);
            return newAuthData;
        } catch (DataAccessException ex) {
            throw new ServerException(ex);
        }
    }

    public void logout(String authToken) throws ServerException {
        try {
            AuthData existingAuth = dataAccessor.getAuthDAO().getAuth(authToken);
            if (existingAuth == null) {
                throw new UnauthorizedException("Error: Invalid token. Unauthorized request");
            }
            dataAccessor.getAuthDAO().deleteAuth(authToken);
        } catch (DataAccessException ex) {
            throw new ServerException(ex);
        }
    }

    private String createAuthToken() {
        byte[] randomBytes = new byte[24];
        SECURE_RANDOM.nextBytes(randomBytes);
        return URL_SAFE_ENCODER.encodeToString(randomBytes);
    }

    private void validateUserData(UserData user) throws BadRequestException {
        if (user == null) {
            throw new BadRequestException("Error: User cannot be null");
        }
        if (user.username() == null) {
            throw new BadRequestException("Error: Username cannot be null");
        }
        if (user.password() == null) {
            throw new BadRequestException("Error: Password cannot be null");
        }
        if (user.email() == null) {
            throw new BadRequestException("Error: Email cannot be null");
        }
    }

    private void checkUsernameAvailability(String username) throws DataAccessException, AlreadyTakenException {
        if (dataAccessor.getUserDAO().userExists(username)) {
            throw new AlreadyTakenException("Error: Username %s is already taken".formatted(username));
        }
    }

    private void validateLoginCredentials(LoginRequest credentials) throws DataAccessException, UnauthorizedException {
        if (!dataAccessor.getUserDAO().userExists(credentials.username())) {
            throw new UnauthorizedException("Error: User %s does not exist".formatted(credentials.username()));
        }
        if (!dataAccessor.getUserDAO().validLogin(credentials)) {
            throw new UnauthorizedException("Error: Wrong password");
        }
    }
}