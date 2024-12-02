package handlers;

import dataaccess.DataAccess;
import model.CreateGameRequest;
import service.GameService;
import service.ServerException;

public class CreateGameHandler extends RequestHandler<CreateGameRequest> {
    private final DataAccess dataAccessor;

    public CreateGameHandler(DataAccess dataAccessor) {
        super(dataAccessor);
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected Class<CreateGameRequest> getRequestClass() {
        return determineRequestType();
    }

    private Class<CreateGameRequest> determineRequestType() {
        return CreateGameRequest.class;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, CreateGameRequest request, String token)
            throws ServerException {
        return handleCreateGameRequest(request, token);
    }

    private Object handleCreateGameRequest(CreateGameRequest createRequest, String authToken) throws ServerException {
        GameService gameService = createGameService();
        return gameService.create(createRequest.gameName(), authToken);
    }

    private GameService createGameService() {
        return new GameService(dataAccessor);
    }
}