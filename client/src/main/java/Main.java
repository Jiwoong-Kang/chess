import client.ServerFacade;
import ui.PreLoginREPL;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("♕ 240 Chess Client: ");

        ServerFacade server = new ServerFacade();
        PreLoginREPL preLogin = new PreLoginREPL(server);
        preLogin.run();
        System.out.println("Exited");

    }
}