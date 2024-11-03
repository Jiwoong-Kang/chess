import chess.*;
import client.serverFacade;
import ui.preLoginREPL;

public class Main {
    public static void main(String[] args) {

        System.out.println("♕ 240 Chess Client: ");

        serverFacade server = new serverFacade();
        preLoginREPL preLogin = new preLoginREPL(server);
        preLogin.run();
        System.out.println("Exited");

    }
}