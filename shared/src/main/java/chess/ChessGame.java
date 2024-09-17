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

    }
    // By using toString, it gives direct recognition which one is while and black.

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
            ChessPiece tempiece = board.getPiece(move.getEndPosition());
            board.addPiece(startPosition, null);
            board.addPiece(move.getEndPosition(), currentPiece);
            if(!isInCheck(currentPiece.getTeamColor())){
                validMoves.add(move);
            }
            board.addPiece(move.getEndPosition(), tempiece);
            board.addPiece(startPosition, currentPiece);
        }
        return validMoves;
    }

    // HashSet<ChessMove> brings all possible paths to go, startPosition becomes its criteria.
    // saves possible moves to valid moves
    // after it moves temporarily, clean the startPosition and endPosition becomes currentPosition.
    // if player's king is safe, that is added to valid move.
    // endPosition went back to normal and its piece's new startPosition is currentPosition.

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

    // goodMoves are from validMoves, if those are not existed, return false because of boolean.
    // if there is a valid move and player's correct turn, check if there is any promotion.
    // the system removes the currentPosition piece and make it exist on where it should move.

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;
        for (int y = 1; y <= 8 && kingPos==null; y++) {
            for (int x = 1; x <= 8 && kingPos==null; x++) {
                ChessPiece currentPiece = board.getPiece(new ChessPosition(y, x));
                if (currentPiece == null) {
                    continue;
                }
                if(currentPiece.getTeamColor() == teamColor && currentPiece.getPieceType() == ChessPiece.PieceType.KING){
                    kingPos = new ChessPosition(y, x);
                }
            }
        }

        // it looks through all parts of the board and if the currentPiece can get the any kings.
        // but if it is a king of the current player, it is fine or if there is no king on the way.

        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                ChessPiece currentPiece = board.getPiece(new ChessPosition(y, x));
                if (currentPiece == null || currentPiece.getTeamColor() == teamColor) {
                    continue;
                }
                for (ChessMove enemyMove: currentPiece.pieceMoves(board, new ChessPosition(y, x))) {
                    if(enemyMove.getEndPosition().equals(kingPos)){
                        return true;
                    }
                }
            }
        }

        // if currentPiece is my team, it should be fine.
        // if enemyMove reaches to my king, it returns true because it is check state.
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && isInStalemate(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for ( int y = 1; y <= 8; y++) {
            for ( int x = 1; x <= 8; x++) {
                ChessPosition currentPosition = new ChessPosition(y, x);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                Collection<ChessMove> moves;

                if(currentPiece != null && teamColor == currentPiece.getTeamColor()){
                    moves = validMoves(currentPosition);
                    if(moves != null && !moves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // bring the currentPosition and check if there is any available movement.
    // if not, it is staleMate.

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

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }
}
