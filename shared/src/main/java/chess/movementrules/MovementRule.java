package chess.movementrules;

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

    static HashSet<ChessMove> staticMoves(ChessPosition currentPosition, int[][] possibleMoves, ChessBoard board) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessGame.TeamColor team = board.getTeamOfSquare(currentPosition);

        for (int[] move : possibleMoves) {
            ChessPosition newPosition = calculateNewPosition(currentPosition, move);
            if (isValidMove(newPosition, team, board)) {
                moves.add(new ChessMove(currentPosition, newPosition, null));
            }
        }
        return moves;
    }

    private static ChessPosition calculateNewPosition(ChessPosition current, int[] move) {
        return new ChessPosition(current.getRow() + move[1], current.getColumn() + move[0]);
    }

    private static boolean isValidMove(ChessPosition position, ChessGame.TeamColor team, ChessBoard board) {
        return MovementRule.inSquare(position) && board.getTeamOfSquare(position) != team;
    }

    // StaticMoves like knight or king

    static HashSet<ChessMove> directionalMoves(ChessBoard board, ChessPosition currPosition,
                                               int[][] moveDirections, int currY, int currX,
                                               ChessGame.TeamColor teamColor) {
        HashSet<ChessMove> moves = new HashSet<>();
        for (int[] direction : moveDirections) {
            boolean problems = false;
            int i = 1;
            while (!problems) {
                ChessPosition possiblePosition = new ChessPosition(currY + direction[1]*i, currX + direction[0]*i);
                if (!MovementRule.inSquare(possiblePosition)) {
                    problems = true;
                }
                else if (board.getPiece(possiblePosition) == null) {
                    moves.add(new ChessMove(currPosition, possiblePosition, null));
                }
                else if (board.getTeamOfSquare(possiblePosition) != teamColor) {
                    moves.add(new ChessMove(currPosition, possiblePosition, null));
                    problems = true;
                }
                else if (board.getTeamOfSquare(possiblePosition) == teamColor) {
                    problems = true;
                }
                else {
                    problems = true;
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