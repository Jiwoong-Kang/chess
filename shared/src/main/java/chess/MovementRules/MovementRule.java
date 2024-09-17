package chess.MovementRules;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.HashSet;

public interface MovementRule {

    static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        return null;
    }

    static boolean inSquare(ChessPosition location) {
        return (location.getRow() >= 1 && location.getRow() <= 8) &&
                (location.getColumn() >= 1 && location.getColumn() <= 8);
    }

    // see if wanted location is on the board, or the outside of the board

    static HashSet<ChessMove> generateStaticMoves(ChessPosition currentPosition, int[][] relativeMoves, ChessBoard board) {
        HashSet<ChessMove> moves = HashSet.newHashSet(8);

        int currX = currentPosition.getColumn();
        int currY = currentPosition.getRow();

        ChessGame.TeamColor team = board.getTeamOfSquare(currentPosition);
        for (int[] relativeMove : relativeMoves) {
            ChessPosition possiblePosition = new ChessPosition(currY + relativeMove[1], currX + relativeMove[0]);
            if (MovementRule.inSquare(possiblePosition) && board.getTeamOfSquare(possiblePosition) != team)
                moves.add(new ChessMove(currentPosition, possiblePosition, null));
        }
        return moves;
    }

    // StaticMoves like knight or king

    static HashSet<ChessMove> generateDirectionalMoves(ChessBoard board, ChessPosition currPosition, int[][] moveDirections, int currY, int currX, ChessGame.TeamColor teamColor) {
        HashSet<ChessMove> moves = HashSet.newHashSet(27);
        for (int[] direction : moveDirections) {
            boolean obstructed = false;
            int i = 1;
            while (!obstructed) {
                ChessPosition possiblePosition = new ChessPosition(currY + direction[1]*i, currX + direction[0]*i);
                if (!MovementRule.inSquare(possiblePosition)) {
                    obstructed = true;
                }
                else if (board.getPiece(possiblePosition) == null) {
                    moves.add(new ChessMove(currPosition, possiblePosition, null));
                }
                else if (board.getTeamOfSquare(possiblePosition) != teamColor) {
                    moves.add(new ChessMove(currPosition, possiblePosition, null));
                    obstructed = true;
                }
                else if (board.getTeamOfSquare(possiblePosition) == teamColor) {
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
    // this is for Queen, Bishop, Rook
    // 27 means it is the maximum movement of Queen.
    // this function continues running until it meets any kinds of obstructed.
    // first, it will be stopped if it is the outside of the board.
    // second, it will be stopped if there is any kinds of pieces whether it is player's team or enemy's team.
    // if the possibleMove place is empty, add that place to moves.
}