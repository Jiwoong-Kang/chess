package ui;

import java.util.List;
import java.util.Arrays;
import model.LoginRequest;
import model.UserData;

public class PreloginUI extends UserInterface {
    PreloginUI() {
        super();
        initializeCommands();
    }

    private void initializeCommands() {
        this.cmds.put("register", new FunctionPair<>(
                Arrays.asList("register", "r"),
                new Arguments(Arrays.asList("username", "password", "email")),
                "Register a new user.",
                this::register
        ));
        this.cmds.put("login", new FunctionPair<>(
                Arrays.asList("login", "l"),
                new Arguments(Arrays.asList("username", "password")),
                "Login to an existing account.",
                this::login
        ));
        this.cmds.put("quit", new FunctionPair<>(
                Arrays.asList("quit"),
                "Quit the program.",
                this::quit
        ));
    }

    private String quit() {
        return EscapeSequences.SET_TEXT_COLOR_RED + "Quitting...";
    }

    private String register(String argString) {
        List<String> args = Arrays.asList(argString.split(" "));
        if (args.size() != 3) {
            return "Invalid number of arguments. Use `help` for command info.";
        }

        UserData userData = new UserData(args.get(0), args.get(1), args.get(2));
        Data.getInstance().getServerFacade().register(userData);
        return "Successfully registered.";
    }

    private String login(String argString) {
        List<String> args = Arrays.asList(argString.split(" "));
        if (args.size() != 2) {
            return "Invalid number of arguments. Use `help` for command info.";
        }

        LoginRequest loginRequest = new LoginRequest(args.get(0), args.get(1));
        Data.getInstance().getServerFacade().login(loginRequest);
        return "Successfully logged in.";
    }
}