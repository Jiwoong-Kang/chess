package handlers;

import dataaccess.DataAccess;
import model.LoginRequest;
import service.ServerException;
import service.UserService;

public class LogoutHandler extends RequestHandler<LoginRequest> {
    private final DataAccess dataAccessor;

    public LogoutHandler(DataAccess dataAccessor) {
        super(dataAccessor);
        this.dataAccessor = dataAccessor;
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return determineRequestType();
    }

    private Class<LoginRequest> determineRequestType() {
        return LoginRequest.class;
    }

    @Override
    public Object getServiceResponse(DataAccess dataAccess, LoginRequest request, String token) throws ServerException {
        return performLogout(token);
    }

    private Object performLogout(String authToken) throws ServerException {
        UserService userService = createUserService();
        userService.logout(authToken);
        return null;
    }

    private UserService createUserService() {
        return new UserService(dataAccessor);
    }
}