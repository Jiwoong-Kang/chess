package handlers;

import dataaccess.DataAccess;
import model.LoginRequest;
import service.ServerException;
import service.UserService;

public class LoginHandler extends RequestHandler<LoginRequest> {
    private final DataAccess dataAccessor;

    public LoginHandler(DataAccess dataAccessor) {
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
        return processLoginRequest(request);
    }

    private Object processLoginRequest(LoginRequest loginRequest) throws ServerException {
        UserService userService = createUserService();
        return userService.login(loginRequest);
    }

    private UserService createUserService() {
        return new UserService(dataAccessor);
    }
}