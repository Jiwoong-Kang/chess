import client.serverFacade;
import ui.PreLoginREPL;

public class Main {
    public static void main(String[] args) {

        System.out.println("♕ 240 Chess Client: ");

        serverFacade server = new serverFacade();
        PreLoginREPL preLogin = new PreLoginREPL(server);
        preLogin.run();
        System.out.println("Exited");

    }
}