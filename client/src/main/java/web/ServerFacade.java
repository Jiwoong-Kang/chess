package web;

import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.Gson;
import chess.ChessGame;
import model.*;
import ui.Data;
import ui.PostloginUI;

public class ServerFacade {
    private final String serverUrl;
    private final Gson jsonSerializer = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData register(UserData user) {
        AuthData authData = executeRequest("/user", "POST", user, AuthData.class);
        updateSessionData(authData, user.username());
        return authData;
    }

    public AuthData login(LoginRequest loginRequest) {
        AuthData authData = executeRequest("/session", "POST", loginRequest, AuthData.class);
        updateSessionData(authData, authData.username());
        return authData;
    }

    public void logout() {
        executeRequest("/session", "DELETE", null, EmptyRequest.class);
        clearSessionData();
    }

    public CreateGameResult createGame(CreateGameRequest createReq) {
        return executeRequest("/game", "POST", createReq, CreateGameResult.class);
    }

    public List<GameData> listGames() {
        GameListResult result = executeRequest("/game", "GET", null, GameListResult.class);
        List<GameData> sortedGames = new ArrayList<>(result.games());
        sortedGames.sort(new GameDataComparator());
        Data.getInstance().setGameList(sortedGames);
        return sortedGames;
    }

    public void joinGame(ChessGame.TeamColor color, int gameNumber) {
        int gameID = Data.getInstance().getGameList().get(gameNumber - 1).gameID();
        JoinGameRequest joinReq = new JoinGameRequest(color, gameID);
        executeRequest("/game", "PUT", joinReq, EmptyRequest.class);
        updateGameData(gameID, gameNumber, color);
    }

    public void observeGame(int gameNumber) {
        int gameID = Data.getInstance().getGameList().get(gameNumber - 1).gameID();
        updateGameData(gameID, gameNumber, null);
    }

    private <T> T executeRequest(String endpointUrl, String method, Object requestBody, Class<T> responseType) {
        try {
            HttpURLConnection connection = setupConnection(endpointUrl, method);
            if (requestBody != null) {
                sendRequestBody(connection, requestBody);
            }
            validateResponse(connection);
            return parseResponse(connection, responseType);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error connecting to server: " + e.getMessage());
        }
    }

    private HttpURLConnection setupConnection(String endpointUrl, String method)
            throws IOException, URISyntaxException {
        URL url = new URI(this.serverUrl).resolve(endpointUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method.toUpperCase());
        connection.addRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        String authToken = Data.getInstance().getAuthToken();
        if (authToken != null) {
            connection.addRequestProperty("Authorization", authToken);
        }

        return connection;
    }

    private void sendRequestBody(HttpURLConnection connection, Object requestBody) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")) {
            writer.write(jsonSerializer.toJson(requestBody));
            writer.flush();
        }
    }

    private void validateResponse(HttpURLConnection connection) throws IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed: " + connection.getResponseMessage());
        }
    }

    private <T> T parseResponse(HttpURLConnection connection, Class<T> responseType) throws IOException {
        if (responseType == null) {return null;}
        try (InputStream inputStream = connection.getInputStream()) {
            String response = new String(inputStream.readAllBytes(), "UTF-8");
            return jsonSerializer.fromJson(response, responseType);
        }
    }

    private void updateSessionData(AuthData authData, String username) {
        Data.getInstance().setAuthToken(authData.authToken());
        Data.getInstance().setUsername(username);
        Data.getInstance().setState(Data.State.LOGGED_IN);
    }

    private void clearSessionData() {
        Data.getInstance().setAuthToken(null);
        Data.getInstance().setUsername(null);
        Data.getInstance().setState(Data.State.LOGGED_OUT);
    }

    private void updateGameData(int gameID, int gameNumber, ChessGame.TeamColor color) {
        Data.getInstance().addGameID(gameID);
        Data.getInstance().setGameNumber(gameNumber);
        Data.getInstance().setColor(color);
        Data.getInstance().setState(Data.State.IN_GAME);
    }

    private static class GameDataComparator implements Comparator<GameData> {
        @Override
        public int compare(GameData o1, GameData o2) {
            int emptyo1 = PostloginUI.emptySpots(o1);
            int emptyo2 = PostloginUI.emptySpots(o2);
            if (emptyo1 != emptyo2) {
                return Integer.compare(emptyo1, emptyo2);
            }
            return Integer.compare(o1.gameID(), o2.gameID());
        }
    }
}