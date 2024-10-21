package chess.movementrules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class RookMovementRule implements MovementRule {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int currX = currentPosition.getColumn();
        int currY = currentPosition.getRow();
        int[][] moveDirections = {{0,1},{1,0},{0,-1},{-1,0}};

        ChessGame.TeamColor team = board.getTeamOfSquare(currentPosition);

        return MovementRule.directionalMoves(board, currentPosition, moveDirections, currY, currX, team);
    }
    // used static to call directly like RookMovementRule.getMoves(board, position)
}
