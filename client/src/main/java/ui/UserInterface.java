package ui;

import java.util.*;
import java.util.stream.Collectors;

public class UserInterface implements BaseUI {

    protected final Map<String, FunctionPair<String>> cmds;

    UserInterface() {
        this.cmds = new LinkedHashMap<>();
        this.cmds.put("help", new FunctionPair<>(Arrays.asList("help", "h"),
                "Displays this help message.", this::help));
    }

    @Override
    public String help() {
        return cmds.entrySet().stream()
                .map(this::formatHelpMessage)
                .collect(Collectors.joining("\n"));
    }

    private String formatHelpMessage(Map.Entry<String, FunctionPair<String>> entry) {
        String key = entry.getKey();
        FunctionPair<String> f = entry.getValue();
        String callers = formatCallers(f.getKeys());
        String args = formatArgs(f.getArgs());
        return String.format("%s%s:%s\n\t%s%s %s%s- %s",
                EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_COLOR_MAGENTA, key,
                EscapeSequences.RESET_TEXT_BOLD_FAINT, EscapeSequences.SET_TEXT_COLOR_BLUE, callers, args,
                EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, f.getHelp());
    }

    private String formatCallers(List<String> callers) {
        return String.format("%s[%s]%s", EscapeSequences.SET_TEXT_BOLD,
                String.join("|", callers), EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private String formatArgs(Object args) {
        return args != null ? EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_TEXT_ITALIC + args.toString()
                + EscapeSequences.RESET_TEXT_BOLD_FAINT + EscapeSequences.RESET_TEXT_ITALIC : "";
    }

    @Override
    public String runCmd(String cmd) {
        String[] parts = cmd.split(" ", 2);
        String cmdCaller = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        return cmds.values().stream()
                .filter(f -> f.getKeys().contains(cmdCaller))
                .findFirst()
                .map(f -> f.apply(args))
                .orElse(help());
    }
}