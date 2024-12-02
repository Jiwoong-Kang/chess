package handlers;

import dataaccess.DataAccess;
import model.JoinGameRequest;
import service.GameService;
import service.ServerException;

public class JoinGameHandler extends RequestHandler<JoinGameRequest> {
    private final DataAccess dataAccessor;

    public JoinGameHandler(DataAccess dataAccessor) {
        super(dataAccessor);
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected Class<JoinGameRequest> getRequestClass() {
        return determineRequestType();
    }

    private Class<JoinGameRequest> determineRequestType() {
        return JoinGameRequest.class;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, JoinGameRequest request, String token)
            throws ServerException {
        return processJoinGameRequest(request, token);
    }

    private Object processJoinGameRequest(JoinGameRequest joinRequest, String authToken) throws ServerException {
        GameService gameService = createGameService();
        return gameService.join(joinRequest.playerColor(), joinRequest.gameID(), authToken);
    }

    private GameService createGameService() {
        return new GameService(dataAccessor);
    }
}