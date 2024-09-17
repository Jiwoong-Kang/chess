package chess.MovementRules;

import chess.*;

import java.util.HashSet;

public class PawnMovementRule implements MovementRule {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition){
        HashSet<ChessMove> moves = HashSet.newHashSet(16);
        int currX = currentPosition.getColumn();
        int currY = currentPosition.getRow();
        ChessPiece.PieceType[] promotionPieces = new ChessPiece.PieceType[]{null};

        ChessGame.TeamColor teamColor = board.getTeamOfSquare(currentPosition);
        int differentMoveByColor = teamColor == ChessGame.TeamColor.WHITE ? 1 : -1;

        boolean promote = (teamColor == ChessGame.TeamColor.WHITE && currY == 7) || (teamColor == ChessGame.TeamColor.BLACK && currY == 2);
        if (promote) {
            promotionPieces = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};
        }

        for (ChessPiece.PieceType promotionPiece : promotionPieces) {
            //Add moving forward, if available
            //Promotion sometimes happens, so it is added by default to avoid making many exceptions
            ChessPosition forwardPosition = new ChessPosition(currY + differentMoveByColor, currX);
            if (MovementRule.inSquare(forwardPosition) && board.getPiece(forwardPosition) == null) {
                moves.add(new ChessMove(currentPosition, forwardPosition, promotionPiece));
            }
            //Add left attack, if available
            ChessPosition leftAttack = new ChessPosition(currY + differentMoveByColor, currX-1);
            if (MovementRule.inSquare(leftAttack) &&
                    board.getPiece(leftAttack) != null &&
                    board.getTeamOfSquare(leftAttack) != teamColor) {
                moves.add(new ChessMove(currentPosition, leftAttack, promotionPiece));
            }
            //Add right attack, if available
            ChessPosition rightAttack = new ChessPosition(currY + differentMoveByColor, currX+1);
            if (MovementRule.inSquare(rightAttack) &&
                    board.getPiece(rightAttack) != null &&
                    board.getTeamOfSquare(rightAttack) != teamColor) {
                moves.add(new ChessMove(currentPosition, rightAttack, promotionPiece));
            }

            //Add first move double, if available
            ChessPosition doubleForwardPosition = new ChessPosition(currY + differentMoveByColor *2, currX);
            if (MovementRule.inSquare(doubleForwardPosition) &&
                    ((teamColor == ChessGame.TeamColor.WHITE && currY == 2) || (teamColor == ChessGame.TeamColor.BLACK && currY == 7)) &&
                    board.getPiece(doubleForwardPosition) == null &&
                    board.getPiece(forwardPosition) == null) {
                moves.add(new ChessMove(currentPosition, doubleForwardPosition, promotionPiece));
            }

        }

        return moves;
    }

    //The reason the pawn's promotion is 7 when it's white and 2 when it's black is because it has to be there to have a chance of it.
    // In the case of white, 8 is the highest, but position 7, which is just before moving up to 8, has the possibility.
    // In the case of black, you have to go to position 1, which is at the bottom, but only 2, which is just before that, has the possibility of promotion.
    // The positions of the chessboard are made up of 1-8, and to prevent this, I balanced it with column-1 and row-1, so when writing the code here, just write it as is.

}
