package chess.MoveCalculators;

import chess.*;

import java.util.HashSet;

public class PawnMoveCalculator implements MoveCalculator {
    public static HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition currentPosition){
        HashSet<ChessMove> moves = HashSet.newHashSet(16);
        int currX = currentPosition.getColumn();
        int currY = currentPosition.getRow();
        ChessPiece.PieceType[] promotionPieces = new ChessPiece.PieceType[]{null};

        ChessGame.TeamColor team = board.getTeamOfSquare(currentPosition);
        int moveIncrement = team == ChessGame.TeamColor.WHITE ? 1 : -1;

        boolean promote = (team == ChessGame.TeamColor.WHITE && currY == 7) || (team == ChessGame.TeamColor.BLACK && currY == 2);
        if (promote) {
            promotionPieces = new ChessPiece.PieceType[]{ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN};
        }

        for (ChessPiece.PieceType promotionPiece : promotionPieces) {
            //Add moving forward, if available
            //Promotion sometimes happens, so it is added by default to avoid making many exceptions
            ChessPosition forwardPosition = new ChessPosition(currY + moveIncrement, currX);
            if (MoveCalculator.isValidSquare(forwardPosition) && board.getPiece(forwardPosition) == null) {
                moves.add(new ChessMove(currentPosition, forwardPosition, promotionPiece));
            }
            //Add left attack, if available
            ChessPosition leftAttack = new ChessPosition(currY + moveIncrement, currX-1);
            if (MoveCalculator.isValidSquare(leftAttack) &&
                    board.getPiece(leftAttack) != null &&
                    board.getTeamOfSquare(leftAttack) != team) {
                moves.add(new ChessMove(currentPosition, leftAttack, promotionPiece));
            }
            //Add right attack, if available
            ChessPosition rightAttack = new ChessPosition(currY + moveIncrement, currX+1);
            if (MoveCalculator.isValidSquare(rightAttack) &&
                    board.getPiece(rightAttack) != null &&
                    board.getTeamOfSquare(rightAttack) != team) {
                moves.add(new ChessMove(currentPosition, rightAttack, promotionPiece));
            }

            //Add first move double, if available
            ChessPosition doubleForwardPosition = new ChessPosition(currY + moveIncrement*2, currX);
            if (MoveCalculator.isValidSquare(doubleForwardPosition) &&
                    ((team == ChessGame.TeamColor.WHITE && currY == 2) || (team == ChessGame.TeamColor.BLACK && currY == 7)) &&
                    board.getPiece(doubleForwardPosition) == null &&
                    board.getPiece(forwardPosition) == null) {
                moves.add(new ChessMove(currentPosition, doubleForwardPosition, promotionPiece));
            }

        }

        return moves;
    }

}
