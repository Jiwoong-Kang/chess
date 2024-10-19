package server;

import com.google.gson.Gson;
import dataAccess.BadRequestException;
import dataAccess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

public class UserHandler {
    private final UserService userService;
    private final Gson gson;

    public UserHandler(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    public String register(Request req, Response resp) throws BadRequestException {
        UserData userData = gson.fromJson(req.body(), UserData.class);

        if (userData.username() == null || userData.password() == null) {
            throw new BadRequestException("No username and/or password given");
        }

        try {
            AuthData authData = userService.createUser(userData);
            resp.status(200);
            return gson.toJson(authData);
        } catch (BadRequestException e) {
            resp.status(403);
            return gson.toJson(new ErrorResponse("Error: already taken"));
        }
    }

    public String login(Request req, Response resp) throws UnauthorizedException, BadRequestException {
        UserData userData = gson.fromJson(req.body(), UserData.class);
        AuthData authData = userService.loginUser(userData);

        resp.status(200);
        return gson.toJson(authData);
    }

    public String logout(Request req, Response resp) throws UnauthorizedException {
        String authToken = getAuthToken(req);
        userService.logoutUser(authToken);
        resp.status(200);
        return "{}";
    }

    private String getAuthToken(Request req) {
        return req.headers("authorization");
    }

    private static class ErrorResponse {
        final String message;
        ErrorResponse(String message) { this.message = message; }
    }
}