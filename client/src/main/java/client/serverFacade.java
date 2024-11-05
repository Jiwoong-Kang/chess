package client;

import model.GameData;
import model.GamesList;
import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class serverFacade {
    private final String baseURL;
    private String authToken;

    public serverFacade() {
        this("http://localhost:8080");
    }

    public serverFacade(String url) {
        this.baseURL = url;
    }

    public boolean register(String username, String password, String email) {
        Map<String, String> body = Map.of("username", username, "password", password, "email", email);
        return handleAuthResponse(sendRequest("POST", "/user", body));
    }

    public boolean login(String username, String password) {
        Map<String, String> body = Map.of("username", username, "password", password);
        return handleAuthResponse(sendRequest("POST", "/session", body));
    }

    public boolean logout() {
        Map<String, Object> response = sendRequest("DELETE", "/session", null);
        if (response.containsKey("Error")) {
            return false;
        }
        authToken = null;
        return true;
    }

    public int createGame(String gameName) {
        Map<String, String> body = Map.of("gameName", gameName);
        Map<String, Object> response = sendRequest("POST", "/game", body);
        if (response.containsKey("Error")) {
            return -1;
        }
        return ((Double) response.get("gameID")).intValue();
    }

    public HashSet<GameData> listGames() {
        String response = sendStringRequest("GET", "/game", null);
        if (response.contains("Error")) {
            return new HashSet<>();
        }
        return new Gson().fromJson(response, GamesList.class).games();
    }

    public boolean joinGame(int gameId, String playerColor) {
        Map<String, Object> body = new HashMap<>();
        body.put("gameID", gameId);
        if (playerColor != null) {
            body.put("playerColor", playerColor);
        }
        Map<String, Object> response = sendRequest("PUT", "/game", body);
        return !response.containsKey("Error");
    }

    private boolean handleAuthResponse(Map<String, Object> response) {
        if (response.containsKey("Error")) {
            return false;
        }
        authToken = (String) response.get("authToken");
        return true;
    }

    private Map<String, Object> sendRequest(String method, String endpoint, Map<String, ?> body) {
        try {
            HttpURLConnection connection = setupConnection(method, endpoint);
            if (body != null) {
                sendRequestBody(connection, body);
            }
            return handleResponse(connection);
        } catch (IOException | URISyntaxException e) {
            return Map.of("Error", e.getMessage());
        }
    }

    private String sendStringRequest(String method, String endpoint, Map<String, ?> body) {
        try {
            HttpURLConnection connection = setupConnection(method, endpoint);
            if (body != null) {
                sendRequestBody(connection, body);
            }
            return readResponseBody(connection);
        } catch (IOException | URISyntaxException e) {
            return "Error: " + e.getMessage();
        }
    }

    private HttpURLConnection setupConnection(String method, String endpoint) throws IOException, URISyntaxException {
        URI uri = new URI(baseURL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod(method);
        if (authToken != null) {
            connection.addRequestProperty("authorization", authToken);
        }
        return connection;
    }

    private void sendRequestBody(HttpURLConnection connection, Map<String, ?> body) throws IOException {
        connection.setDoOutput(true);
        connection.addRequestProperty("Content-Type", "application/json");
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(new Gson().toJson(body).getBytes());
        }
    }

    private Map<String, Object> handleResponse(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() == 401) {
            return Map.of("Error", 401);
        }
        try (InputStream responseBody = connection.getInputStream()) {
            return new Gson().fromJson(new InputStreamReader(responseBody), Map.class);
        }
    }

    private String readResponseBody(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() == 401) {
            return "Error: 401";
        }
        try (InputStream responseBody = connection.getInputStream()) {
            return new BufferedReader(new InputStreamReader(responseBody))
                    .lines()
                    .reduce("", (accumulator, actual) -> accumulator + actual);
        }
    }
}