package websocket.messages;

public class ErrorMsg extends ServerMessage {

    String errorMessage;

    public ErrorMsg(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }
    public String getMessage() {
        return errorMessage;
    }
}