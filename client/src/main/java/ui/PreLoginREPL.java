package ui;

import client.ServerFacade;

import java.util.Scanner;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class PreLoginREPL {
    private final ServerFacade server;
    private final PostLoginREPL postloginREPL;

    public PreLoginREPL(ServerFacade server) {
        this.server = server;
        this.postloginREPL = new PostLoginREPL(server);
    }

    public void run() {
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to Chess! Enter 'help' to get started.");
        boolean running = true;
        while (running) {
            String[] input = getUserInput();
            boolean shouldExit = processCommand(input);
            if (shouldExit) {
                if (input[0].equals("quit")) {
                    out.println("Exiting the game. Goodbye!");
                    System.exit(0);
                } else {
                    postloginREPL.run();
                    return;
                }
            }
        }
    }

    private boolean processCommand(String[] input) {
        return switch (input[0]) {
            case "quit" -> true;
            case "help" -> {
                printHelpMenu();
                yield false;
            }
            case "login" -> handleLogin(input);
            case "register" -> handleRegister(input);
            default -> {
                handleUnknownCommand();
                yield false;
            }
        };
    }

    private String[] getUserInput() {
        out.print("\n[LOGGED OUT] >>> ");
        return new Scanner(System.in).nextLine().split(" ");
    }

    private boolean handleLogin(String[] input) {
        if (input.length != 3) {
            out.println("Please provide a username and password");
            printLogin();
            return false;
        }
        if (server.login(input[1], input[2])) {
            out.println("You are now logged in");
            return true;
        } else {
            out.println("Username or password incorrect, please try again");
            printLogin();
            return false;
        }
    }

    private boolean handleRegister(String[] input) {
        if (input.length != 4) {
            out.println("Please provide a username, password, and email");
            printRegister();
            return false;
        }
        if (server.register(input[1], input[2], input[3])) {
            out.println("You are now registered and logged in");
            return true;
        } else {
            out.println("Username already in use, please choose a new one");
            printRegister();
            return false;
        }
    }

    private void handleUnknownCommand() {
        out.println("Command not recognized, please try again");
        printHelpMenu();
    }

    private void printHelpMenu() {
        printRegister();
        printLogin();
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printRegister() {
        out.println("register <USERNAME> <PASSWORD> <EMAIL> - create a new user");
    }

    private void printLogin() {
        out.println("login <USERNAME> <PASSWORD> - login to an existing user");
    }
}