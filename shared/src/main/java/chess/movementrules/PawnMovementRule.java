package chess.movementrules;

import chess.*;

import java.util.HashSet;

public class PawnMovementRule implements MovementRule {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        HashSet<ChessMove> moves = new HashSet<>(16);
        ChessGame.TeamColor teamColor = board.getTeamOfSquare(currentPosition);
        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        boolean canPromote = canPromote(currentPosition, teamColor);

        forwardMoves(board, currentPosition, direction, canPromote, moves);
        attackMoves(board, currentPosition, direction, canPromote, moves);
        doubleMoveIfPossible(board, currentPosition, direction, moves);

        return moves;
    }

    private static boolean canPromote(ChessPosition position, ChessGame.TeamColor teamColor) {
        return (teamColor == ChessGame.TeamColor.WHITE && position.getRow() == 7) ||
                (teamColor == ChessGame.TeamColor.BLACK && position.getRow() == 2);
    }

    private static void forwardMoves(ChessBoard board, ChessPosition currentPosition, int direction, boolean canPromote, HashSet<ChessMove> moves) {
        ChessPosition forwardPosition = new ChessPosition(currentPosition.getRow() + direction, currentPosition.getColumn());
        if (MovementRule.inSquare(forwardPosition) && board.getPiece(forwardPosition) == null) {
            moveWithPossiblePromotion(currentPosition, forwardPosition, canPromote, moves);
        }
    }

    private static void attackMoves(ChessBoard board, ChessPosition currentPosition, int direction, boolean canPromote, HashSet<ChessMove> moves) {
        int[] attackColumns = {-1, 1};
        for (int columnOffset : attackColumns) {
            ChessPosition attackPosition = new ChessPosition(
                    currentPosition.getRow() + direction,
                    currentPosition.getColumn() + columnOffset
            );
            if (MovementRule.inSquare(attackPosition) &&
                    board.getPiece(attackPosition) != null &&
                    board.getTeamOfSquare(attackPosition) != board.getTeamOfSquare(currentPosition)) {
                moveWithPossiblePromotion(currentPosition, attackPosition, canPromote, moves);
            }
        }
    }

    private static void doubleMoveIfPossible(ChessBoard board, ChessPosition currentPosition, int direction, HashSet<ChessMove> moves) {
        boolean isInitialPosition = (direction == 1 && currentPosition.getRow() == 2) ||
                (direction == -1 && currentPosition.getRow() == 7);
        if (isInitialPosition) {
            ChessPosition singleForward = new ChessPosition(currentPosition.getRow() + direction, currentPosition.getColumn());
            ChessPosition doubleForward = new ChessPosition(currentPosition.getRow() + 2 * direction, currentPosition.getColumn());
            if (MovementRule.inSquare(doubleForward) &&
                    board.getPiece(singleForward) == null &&
                    board.getPiece(doubleForward) == null) {
                moves.add(new ChessMove(currentPosition, doubleForward, null));
            }
        }
    }

    private static void moveWithPossiblePromotion(ChessPosition from, ChessPosition to, boolean canPromote, HashSet<ChessMove> moves) {
        if (canPromote) {
            for (ChessPiece.PieceType promotionPiece : new ChessPiece.PieceType[]{
                    ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT,
                    ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN}) {
                moves.add(new ChessMove(from, to, promotionPiece));
            }
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

}
