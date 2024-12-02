package ui;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import chess.ChessGame;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;

public class PostloginUI extends GameRendererUI {
    PostloginUI() {
        super();
        initializeCommands();
        Data.getInstance().setColor(null);
    }

    private void initializeCommands() {
        Map<String, FunctionPair<String>> commandMap = new LinkedHashMap<>();
        commandMap.put("create game", new FunctionPair<>(List.of("create", "c"),
                new Arguments(List.of("game_name")), "Create a new game.", this::createGame));
        commandMap.put("list games", new FunctionPair<>(List.of("list","l"), "List all games.", this::listGames));
        commandMap.put("join game", new FunctionPair<>(List.of("join","j"),
                new Arguments(List.of("WHITE|BLACK", "game_number")), "Join a game with the given number.",
                this::joinGame));
        commandMap.put("observe game", new FunctionPair<>(List.of("observe","o"),
                new Arguments(List.of("game_number")), "Observe a game with the given number.", this::observeGame));
        commandMap.put("logout", new FunctionPair<>(List.of("logout"), "Sign out of your account.", this::logout));

        this.cmds.putAll(commandMap);
    }

    private String createGame(String argString) {
        String[] args = argString.split(" ");
        if (args.length != 1) {
            return "Invalid number of arguments. Use `help` for command info.";
        }

        CreateGameResult res = Data.getInstance().getServerFacade().createGame(new CreateGameRequest(argString));
        return String.format("%sSuccessfully created new game%s",
                EscapeSequences.SET_TEXT_COLOR_GREEN, EscapeSequences.RESET_TEXT_COLOR);
    }

    private String listGames() {
        List<GameData> games = Data.getInstance().getServerFacade().listGames();
        if (games.isEmpty()) {
            return "No games found. Create one with `creategame`.";
        }

        StringBuilder output = new StringBuilder(EscapeSequences.SET_TEXT_BOLD +
                EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Games:\n" + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            output.append(formatGameInfo(game, i));
        }

        return output.toString();
    }

    private String formatGameInfo(GameData game, int index) {
        String white = formatPlayerName(game.whiteUsername(), true);
        String black = formatPlayerName(game.blackUsername(), false);
        String gameColor = (emptySpots(game) == 0) ? EscapeSequences.SET_TEXT_COLOR_YELLOW
                : EscapeSequences.SET_TEXT_COLOR_GREEN;
        String gameName = EscapeSequences.SET_TEXT_BOLD + game.gameName();
        String gameNumber = EscapeSequences.SET_TEXT_ITALIC + "(" + (index + 1) + ")" +
                EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_TEXT_ITALIC;

        return String.format("\t%s %s %s : %s vs. %s\n", gameColor, gameNumber, gameName, white, black);
    }

    private String formatPlayerName(String username, boolean isWhite) {
        String name = (username == null) ? "Empty" : username;
        String textColor = isWhite ? EscapeSequences.SET_TEXT_COLOR_BLACK : EscapeSequences.SET_TEXT_COLOR_WHITE;
        String bgColor = isWhite ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
        return textColor + bgColor + name + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;
    }

    private String joinGame(String argString) {
        String[] args = argString.split(" ");
        if (args.length != 2) {
            return "Invalid number of arguments. Use `help` for command info.";
        }
        String colorArg = args[0].toLowerCase();
        if (!colorArg.equals("white") && !colorArg.equals("black")) {
            return "Wrong color. Choose the color between white and black";
        }

        ChessGame.TeamColor color = args[0].toLowerCase().equals("white") ? ChessGame.TeamColor.WHITE
                : ChessGame.TeamColor.BLACK;
        int gameNumber = Integer.parseInt(args[1]);
        Data.getInstance().getServerFacade().joinGame(color, gameNumber);

        try {
            Data.getInstance().getWebSocketClient().connect();
        } catch (Exception e) {
            System.out.println("Failed to connect to the web socket server");
        }

        return "";
    }

    public static int emptySpots(GameData game) {
        return (game.whiteUsername() == null ? 0 : 1) + (game.blackUsername() == null ? 0 : 1);
    }

    private String observeGame(String argString) {
        String[] args = argString.split(" ");
        if (args.length != 1) {
            return "Invalid number of arguments. Use `help` for command info.";
        }

        int gameNumber = Integer.parseInt(args[0]);
        Data.getInstance().getServerFacade().observeGame(gameNumber);

        try {
            Data.getInstance().getWebSocketClient().connect();
        } catch (Exception e) {
            System.out.println("Failed to connect to the web socket server");
        }

        return "";
    }

    private String logout() {
        Data.getInstance().getServerFacade().logout();
        return EscapeSequences.SET_TEXT_COLOR_RED + "Successfully logged out." + EscapeSequences.RESET_TEXT_COLOR;
    }
}