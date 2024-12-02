package handlers;

import dataaccess.DataAccess;
import model.EmptyRequest;
import service.ClearService;
import service.ServerException;

public class ClearHandler extends RequestHandler<EmptyRequest> {
    private final DataAccess dataAccessor;

    public ClearHandler(DataAccess dataAccessor) {
        super(dataAccessor);
        this.dataAccessor = dataAccessor;
    }

    @Override
    protected Class<EmptyRequest> getRequestClass() {
        return determineRequestType();
    }

    private Class<EmptyRequest> determineRequestType() {
        return null;
    }

    @Override
    protected Object getServiceResponse(DataAccess dataAccess, EmptyRequest request, String token)
            throws ServerException {
        return executeClearOperation();
    }

    private Object executeClearOperation() throws ServerException {
        ClearService clearService = createClearService();
        clearService.clear();
        return null;
    }

    private ClearService createClearService() {
        return new ClearService(dataAccessor);
    }
}