package handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

public class ServerExceptionHandler<T extends Exception> implements ExceptionHandler<T> {

    private final int statusCode;
    private final Gson jsonConverter;

    public ServerExceptionHandler(int statusCode) {
        this.statusCode = statusCode;
        this.jsonConverter = new Gson();
    }

    @Override
    public void handle(T exception, Request req, Response res) {
        logExceptionIfNecessary(exception);
        setResponseStatusAndBody(res, exception);
    }

    private void logExceptionIfNecessary(T exception) {
        if (exception.getCause() != null) {
            exception.printStackTrace();
        }
    }

    private void setResponseStatusAndBody(Response res, T exception) {
        res.status(statusCode);
        res.body(createJsonErrorMessage(exception));
    }

    private String createJsonErrorMessage(T exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", exception.getMessage());
        return jsonConverter.toJson(errorMap);
    }
}