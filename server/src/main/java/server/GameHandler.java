package server;

import dataAccess.GameDAO;

import service.GameService;

public class GameHandler {

    GameService gameService;
    public GameHandler(GameService gameService){
        this.gameService = gameService;
    }
}
