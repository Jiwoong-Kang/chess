package ui;

import client.serverFacade;

import java.util.Scanner;
import static java.lang.System.out;
import static ui.EscapeSequences.*;

public class PreLoginREPL {
    private final serverFacade server;
    private final PostLoginREPL postloginREPL;

    public PreLoginREPL(serverFacade server) {
        this.server = server;
        this.postloginREPL = new PostLoginREPL(server);
    }

    public void run() {
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        out.println("Welcome to Chess! Enter 'help' to get started.");

        while (true) {
            String[] input = getUserInput();
            if (processCommand(input)) {
                break;
            }
        }

        postloginREPL.run();
    }

    private boolean processCommand(String[] input) {
        switch (input[0]) {
            case "quit":
                return true;
            case "help":
                printHelpMenu();
                return false;
            case "login":
                return handleLogin(input);
            case "register":
                return handleRegister(input);
            default:
                handleUnknownCommand();
                return false;
        }
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