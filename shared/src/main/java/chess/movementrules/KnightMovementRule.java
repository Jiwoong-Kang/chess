package chess.movementrules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class KnightMovementRule implements MovementRule {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition){
        int[][] possibleMoves = {{-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}};
        return MovementRule.StaticMoves(currentPosition, possibleMoves, board);
    }
}
