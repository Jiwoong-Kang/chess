package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private boolean gameOver;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        public String toString() {
            return this == WHITE ? "white" : "black";
        }

    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    // all types of available moves collection
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = board.getPiece(startPosition);
        if (currentPiece == null) {
            return null;
        }
        HashSet<ChessMove> possibleMoves = (HashSet<ChessMove>) board.getPiece(startPosition).pieceMoves(board, startPosition);
        HashSet<ChessMove> validMoves = HashSet.newHashSet(possibleMoves.size());
        for (ChessMove move : possibleMoves) {
            ChessPiece tempPiece = board.getPiece(move.getEndPosition());
            board.addPiece(startPosition, null);
            board.addPiece(move.getEndPosition(), currentPiece);
            if(!isInCheck(currentPiece.getTeamColor())){
                validMoves.add(move);
            }
            board.addPiece(move.getEndPosition(), tempPiece);
            board.addPiece(startPosition, currentPiece);
        }
        return validMoves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean isTeamsTurn = getTeamTurn() == board.getTeamOfSquare(move.getStartPosition());
        Collection<ChessMove> goodMoves = validMoves(move.getStartPosition());
        if (goodMoves == null){
            throw new InvalidMoveException("No valid moves available");
        }
        boolean isValidMove = goodMoves.contains(move);

        if(isValidMove && isTeamsTurn){
            ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
            if( move.getPromotionPiece() != null){
                pieceToMove = new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
            }

            board.addPiece(move.getStartPosition(), null);
            board.addPiece(move.getEndPosition(), pieceToMove);
            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        }
        else{
            throw new InvalidMoveException(String.format("Valid move: %b Your Turn: %b", isValidMove, isTeamsTurn));
        }
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKingPosition(teamColor);
        if (kingPos == null) {
            return false;
        }

        return canAnyEnemyPieceReachPosition(teamColor, kingPos);
    }

    private ChessPosition findKingPosition(TeamColor teamColor) {
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                ChessPosition position = new ChessPosition(y, x);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    private boolean canAnyEnemyPieceReachPosition(TeamColor teamColor, ChessPosition targetPosition) {
        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                if (canEnemyPieceAtPositionReachTarget(teamColor, new ChessPosition(y, x), targetPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canEnemyPieceAtPositionReachTarget(TeamColor teamColor, ChessPosition position, ChessPosition targetPosition) {
        ChessPiece piece = board.getPiece(position);
        if (piece == null || piece.getTeamColor() == teamColor) {
            return false;
        }
        return piece.pieceMoves(board, position).stream()
                .anyMatch(move -> move.getEndPosition().equals(targetPosition));
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    private boolean novalidmove(TeamColor teamColor) {
        for (int y = 1; y <= 8; y++){
            for (int x = 1; x <= 8; x++) {
                ChessPosition currentPosition = new ChessPosition(y, x);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && teamColor == currentPiece.getTeamColor()) {
                    Collection<ChessMove> moves = validMoves(currentPosition);
                    if ( moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && novalidmove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && novalidmove(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    public boolean getGameOver() {
        return gameOver;
    }


    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
