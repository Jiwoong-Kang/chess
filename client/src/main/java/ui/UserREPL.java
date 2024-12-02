package ui;

import java.util.Scanner;
import javax.websocket.OnMessage;
import com.google.gson.Gson;
import web.WebSocketObserver;
import websocket.messages.*;

public class UserREPL implements WebSocketObserver {
    private final Gson jsonParser = new Gson();

    @Override
    @OnMessage
    public void receiveMessage(String msg) {
        ServerMessage message = jsonParser.fromJson(msg, ServerMessage.class);
        processServerMessage(message, msg);
        displayPrompt();
    }

    private void processServerMessage(ServerMessage message, String rawMsg) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> handleNotification(rawMsg);
            case ERROR -> handleError(rawMsg);
            case LOAD_GAME -> handleLoadGame(rawMsg);
            default -> System.out.println("Unknown message type: " + message.getServerMessageType());
        }
    }

    private void handleNotification(String rawMsg) {
        Notification notif = jsonParser.fromJson(rawMsg, Notification.class);
        displayColoredMessage(notif.getMessage(), EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

    private void handleError(String rawMsg) {
        ErrorMsg error = jsonParser.fromJson(rawMsg, ErrorMsg.class);
        displayColoredMessage(error.getErrorMessage(), EscapeSequences.SET_TEXT_COLOR_RED);
    }

    private void handleLoadGame(String rawMsg) {
        LoadGame gameMsg = jsonParser.fromJson(rawMsg, LoadGame.class);
        Data.getInstance().setGame(gameMsg.getGame());
        System.out.println();
        System.out.println(((GameUI) Data.getInstance().getUi()).formatBoard());
        Data.getInstance().setJustMoved(false);
    }

    private void displayColoredMessage(String message, String color) {
        System.out.println(String.format("%s%s%s", color, message, EscapeSequences.RESET_TEXT_COLOR));
    }

    private void displayPrompt() {
        String prompt = String.format("\r%s%s%s",
                EscapeSequences.SET_TEXT_COLOR_YELLOW,
                Data.getInstance().getPrompt(),
                EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(prompt);
        System.out.flush();
    }

    public void run() {
        displayWelcomeMessage();
        processUserInput();
    }

    private void displayWelcomeMessage() {
        System.out.printf("%sWelcome to Chess240! Type 'help' for a list of commands. Login to get started!\n",
                EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    private void processUserInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            String userInput = "";
            while (!userInput.equals("quit")) {
                displayPrompt();
                userInput = scanner.nextLine().toLowerCase();
                executeCommand(userInput);
            }
        }
    }

    private void executeCommand(String input) {
        try {
            String result = Data.getInstance().getUi().runCmd(input);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }
}