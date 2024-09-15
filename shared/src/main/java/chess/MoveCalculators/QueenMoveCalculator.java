package chess.MoveCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public class QueenMoveCalculator implements MoveCalculator {

    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        int currX = currentPosition.getColumn();
        int currY = currentPosition.getRow();
        int [][] moveDirections = {{-1,-1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1},{-1,0}};

        ChessGame.TeamColor team = board.getTeamOfSquare(currentPosition);

        return MoveCalculator.generateDirectionalMoves(board, currentPosition, moveDirections, currY, currX, team);
    }
}
