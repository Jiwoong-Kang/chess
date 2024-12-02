package handlers;

import dataaccess.DataAccess;
import model.EmptyRequest;
import service.GameService;
import service.ServerException;

public class ListGamesHandler extends RequestHandler<EmptyRequest> {
    private final DataAccess dataAccessor;

    public ListGamesHandler(DataAccess dataAccessor) {
        super(dataAccessor);
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected Class<EmptyRequest> getRequestClass() {
        return determineRequestType();
    }

    private Class<EmptyRequest> determineRequestType() {
        return EmptyRequest.class;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, EmptyRequest request, String token)
            throws ServerException {
        return retrieveGamesList(token);
    }

    private Object retrieveGamesList(String authToken) throws ServerException {
        GameService gameService = createGameService();
        return gameService.list(authToken);
    }

    private GameService createGameService() {
        return new GameService(dataAccessor);
    }
}