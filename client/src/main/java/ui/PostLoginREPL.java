package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.*;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class PostLoginREPL {

    private final ServerFacade server;
    private List<GameData> games;

    public PostLoginREPL(ServerFacade server) {
        this.server = server;
        this.games = new ArrayList<>();
    }

    public void run() {
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        boolean running = true;
        String[] lastInput = null;
        while (running) {
            lastInput = getUserInput();
            running = processCommand(lastInput);
        }
        if (lastInput[0].equals("quit")){
            out.println("Exiting the game. Goodbye!");
            System.exit(0);
        }
        else {
            new PreLoginREPL(server).run();
        }
    }

    private boolean processCommand(String[] input) {
        switch (input[0]) {
            case "quit":
                return false;
            case "help":
                printHelpMenu();
                break;
            case "logout":
                server.logout();
                return false;
            case "list":
                handleListCommand();
                break;
            case "create":
                handleCreateCommand(input);
                break;
            case "join":
                handleJoinCommand(input);
                break;
            case "observe":
                handleObserveCommand(input);
                break;
            default:
                handleUnknownCommand();
                break;
        }
        return true;
    }

    private String[] getUserInput() {
        out.print("\n[LOGGED IN] >>> ");
        return new Scanner(System.in).nextLine().split(" ");
    }

    private void handleListCommand() {
        refreshGames();
        printGames();
    }

    private void handleCreateCommand(String[] input) {
        if (input.length != 2) {
            out.println("Please provide a name");
            printCreate();
            return;
        }
        String gameName = input[1];
        server.createGame(gameName);
        out.printf("%s created successfully", gameName);
    }

    private void handleJoinCommand(String[] input) {
        if (input.length != 3) {
            out.println("Please provide a game ID and color choice");
            printJoin();
            return;
        }
        joinGame(Integer.parseInt(input[1]), input[2].toUpperCase());
    }

    private void handleObserveCommand(String[] input) {
        if (input.length != 2) {
            out.println("Please provide a game ID");
            printObserve();
            return;
        }
        observeGame(Integer.parseInt(input[1]));
    }

    private void handleUnknownCommand() {
        out.println("Command not recognized, please try again");
        printHelpMenu();
    }

    private void refreshGames() {
        games = new ArrayList<>(server.listGames());
    }

    private void printGames() {
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
            out.printf("%d -- Game Name: %s  |  White User: %s  |  Black User: %s %n", i+1, game.gameName(), whiteUser, blackUser);
        }
    }

    private void joinGame(int gameIndex, String color) {
        GameData joinGame = games.get(gameIndex);
        if (server.joinGame(joinGame.gameID(), color)) {
            out.println("You have joined the game");
            new BoardPrinter(new ChessGame().getBoard()).printBoard();
            // originally it was new BoardPrinter(joinGame.game().getBoard()).printBoard();
        } else {
            out.println("Game does not exist or color taken");
            printJoin();
        }
    }

    private void observeGame(int gameIndex) {
        GameData observeGame = games.get(gameIndex);
        if (server.joinGame(observeGame.gameID(), null)) {
            out.println("You have joined the game as an observer");
            new BoardPrinter(observeGame.game().getBoard()).printBoard();
        } else {
            out.println("Game does not exist");
            printObserve();
        }
    }

    private void printHelpMenu() {
        printCreate();
        out.println("list - list all games");
        printJoin();
        printObserve();
        out.println("logout - log out of current user");
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printCreate() {
        out.println("create <NAME> - create a new game");
    }

    private void printJoin() {
        out.println("join <ID> [WHITE|BLACK] - join a game as color");
    }

    private void printObserve() {
        out.println("observe <ID> - observe a game");
    }
}