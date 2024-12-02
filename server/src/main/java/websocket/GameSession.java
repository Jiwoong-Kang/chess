package websocket;

import model.AuthData;
import model.GameData;

public class GameSession {
    private final AuthData authData;
    private final GameData gameData;

    public GameSession(AuthData authData, GameData gameData) {
        this.authData = authData;
        this.gameData = gameData;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public GameData getGameData() {
        return gameData;
    }
}