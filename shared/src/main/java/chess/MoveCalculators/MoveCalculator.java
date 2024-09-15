package chess.MoveCalculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public interface MoveCalculator {

    static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currPosition) {
        return null;
    }

    static boolean isValidSquare(ChessPosition position) {
        return (position.getRow() >= 1 && position.getRow() <= 8) &&
                (position.getColumn() >= 1 && position.getColumn() <= 8);
    }

    static HashSet<ChessMove> generateStaticMoves(ChessPosition currPosition, int[][] relativeMoves, ChessBoard board) {
        HashSet<ChessMove> moves = HashSet.newHashSet(8);

        int currX = currPosition.getColumn();
        int currY = currPosition.getRow();

        ChessGame.TeamColor team = board.getTeamOfSquare(currPosition);
        for (int[] relativeMove : relativeMoves) {
            ChessPosition possiblePosition = new ChessPosition(currY + relativeMove[1], currX + relativeMove[0]);
            if (MoveCalculator.isValidSquare(possiblePosition) && board.getTeamOfSquare(possiblePosition) != team)
                moves.add(new ChessMove(currPosition, possiblePosition, null));
        }
        return moves;
    }

    static HashSet<ChessMove> generateDirectionalMoves(ChessBoard board, ChessPosition currPosition, int[][] moveDirections, int currY, int currX, ChessGame.TeamColor team) {
        HashSet<ChessMove> moves = HashSet.newHashSet(27);
        for (int[] direction : moveDirections) {
            boolean obstructed = false;
            int i = 1;
            while (!obstructed) {
                ChessPosition possiblePosition = new ChessPosition(currY + direction[1]*i, currX + direction[0]*i);
                if (!MoveCalculator.isValidSquare(possiblePosition)) {
                    obstructed = true;
                }
                else if (board.getPiece(possiblePosition) == null) {
                    moves.add(new ChessMove(currPosition, possiblePosition, null));
                }
                else if (board.getTeamOfSquare(possiblePosition) != team) {
                    moves.add(new ChessMove(currPosition, possiblePosition, null));
                    obstructed = true;
                }
                else if (board.getTeamOfSquare(possiblePosition) == team) {
                    obstructed = true;
                }
                else {
                    obstructed = true;
                }
                i++;
            }
        }
        return moves;
    }

}