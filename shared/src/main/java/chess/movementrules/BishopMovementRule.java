package chess.movementrules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class BishopMovementRule implements MovementRule {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition){
        int currX = currentPosition.getColumn();
        int currY = currentPosition.getRow();
        int[][] moveDirections = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};

        ChessGame.TeamColor team = board.getTeamOfSquare(currentPosition);

        return MovementRule.DirectionalMoves(board, currentPosition, moveDirections, currY, currX, team);
    }
}
