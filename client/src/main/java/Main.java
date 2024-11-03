import chess.*;
import client.serverFacade;
import ui.preLoginREPL;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        serverFacade server = new serverFacade();
        preLoginREPL preLogin = new preLoginREPL(server);
        preLogin.run();
        System.out.println("Exited");

    }
}