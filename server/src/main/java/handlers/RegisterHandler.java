package handlers;

import dataaccess.DataAccess;
import model.AuthData;
import model.UserData;
import service.ServerException;
import service.UserService;

public class RegisterHandler extends RequestHandler<UserData> {
    private final DataAccess dataAccessor;

    public RegisterHandler(DataAccess dataAccessor) {
        super(dataAccessor);
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected Class<UserData> getRequestClass() {
        return determineRequestType();
    }

    private Class<UserData> determineRequestType() {
        return UserData.class;
    }

    @Override
    protected AuthData getServiceResponse(DataAccess dataAccess, UserData request, String token)
            throws ServerException {
        return processRegistration(request);
    }

    private AuthData processRegistration(UserData userData) throws ServerException {
        UserService userService = createUserService();
        return userService.register(userData);
    }

    private UserService createUserService() {
        return new UserService(dataAccessor);
    }
}