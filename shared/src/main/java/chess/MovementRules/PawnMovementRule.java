package chess.MovementRules;

import chess.*;

import java.util.HashSet;

public class PawnMovementRule implements MovementRule {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition) {
        HashSet<ChessMove> moves = new HashSet<>(16);
        ChessGame.TeamColor teamColor = board.getTeamOfSquare(currentPosition);
        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        boolean canPromote = canPromote(currentPosition, teamColor);

        addForwardMoves(board, currentPosition, direction, canPromote, moves);
        addAttackMoves(board, currentPosition, direction, canPromote, moves);
        addDoubleMoveIfPossible(board, currentPosition, direction, moves);

        return moves;
    }

    private static boolean canPromote(ChessPosition position, ChessGame.TeamColor teamColor) {
        return (teamColor == ChessGame.TeamColor.WHITE && position.getRow() == 7) ||
                (teamColor == ChessGame.TeamColor.BLACK && position.getRow() == 2);
    }

    private static void addForwardMoves(ChessBoard board, ChessPosition currentPosition, int direction, boolean canPromote, HashSet<ChessMove> moves) {
        ChessPosition forwardPosition = new ChessPosition(currentPosition.getRow() + direction, currentPosition.getColumn());
        if (MovementRule.inSquare(forwardPosition) && board.getPiece(forwardPosition) == null) {
            addMoveWithPossiblePromotion(currentPosition, forwardPosition, canPromote, moves);
        }
    }

    private static void addAttackMoves(ChessBoard board, ChessPosition currentPosition, int direction, boolean canPromote, HashSet<ChessMove> moves) {
        int[] attackColumns = {-1, 1};
        for (int columnOffset : attackColumns) {
            ChessPosition attackPosition = new ChessPosition(
                    currentPosition.getRow() + direction,
                    currentPosition.getColumn() + columnOffset
            );
            if (MovementRule.inSquare(attackPosition) &&
                    board.getPiece(attackPosition) != null &&
                    board.getTeamOfSquare(attackPosition) != board.getTeamOfSquare(currentPosition)) {
                addMoveWithPossiblePromotion(currentPosition, attackPosition, canPromote, moves);
            }
        }
    }

    private static void addDoubleMoveIfPossible(ChessBoard board, ChessPosition currentPosition, int direction, HashSet<ChessMove> moves) {
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

    private static void addMoveWithPossiblePromotion(ChessPosition from, ChessPosition to, boolean canPromote, HashSet<ChessMove> moves) {
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
    //The reason the pawn's promotion is 7 when it's white and 2 when it's black is because it has to be there to have a chance of it.
    // In the case of white, 8 is the highest, but position 7, which is just before moving up to 8, has the possibility.
    // In the case of black, you have to go to position 1, which is at the bottom, but only 2, which is just before that, has the possibility of promotion.
    // The positions of the chessboard are made up of 1-8, and to prevent this, I balanced it with column-1 and row-1, so when writing the code here, just write it as is.

}
