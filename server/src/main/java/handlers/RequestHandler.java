package handlers;

import java.net.HttpURLConnection;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import service.ServerException;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class RequestHandler<T> implements Route {
    protected final DataAccess dataAccess;

    protected RequestHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @Override
    public Object handle(Request request, Response response) throws ServerException {
        return processRequest(request, response);
    }

    private Object processRequest(Request request, Response response) throws ServerException {
        Gson serializer = createSerializer();
        String token = extractAuthToken(request);
        T reqObj = deserializeRequest(request, serializer);
        Object res = executeService(reqObj, token);
        setResponseStatus(response);
        return serializeResponse(res, serializer);
    }

    private Gson createSerializer() {
        return new Gson();
    }

    private String extractAuthToken(Request request) {
        return request.headers("Authorization");
    }

    private T deserializeRequest(Request request, Gson serializer) {
        Class<T> reqCls = getRequestClass();
        return (reqCls != null) ? serializer.fromJson(request.body(), reqCls) : null;
    }

    private Object executeService(T reqObj, String token) throws ServerException {
        return getServiceResponse(dataAccess, reqObj, token);
    }

    private void setResponseStatus(Response response) {
        response.status(HttpURLConnection.HTTP_OK);
    }

    private String serializeResponse(Object res, Gson serializer) {
        return serializer.toJson(res);
    }

    protected abstract Class<T> getRequestClass();

    protected abstract Object getServiceResponse(DataAccess dataAccess, T request, String token) throws ServerException;
}