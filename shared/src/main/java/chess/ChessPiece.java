package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece current_piece = board.getPiece(myPosition);
        return switch (current_piece.type) {
            case BISHOP -> bishopRules(board, myPosition);
            case ROOK -> rookRules(board, myPosition);
            case QUEEN -> queenRules(board, myPosition);
            case KING -> kingRules(board, myPosition);
            case KNIGHT -> knightRules(board, myPosition);
            case PAWN -> pawnRules(board, myPosition);
            case null, default -> null;
        };
    }


    private HashSet<ChessMove> bishopRules(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        getDiagonals(board, myPosition, moves);
        return moves;
    }

    private HashSet<ChessMove> rookRules(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        getHorizontals(board, myPosition, moves);
        return moves;
    }

    private HashSet<ChessMove> queenRules(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        getDiagonals(board, myPosition, moves);
        getHorizontals(board, myPosition, moves);
        return moves;
    }

    private HashSet<ChessMove> kingRules(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() + 1, myPosition.getColumn() + 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() + 1, myPosition.getColumn() - 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() - 1, myPosition.getColumn() + 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() - 1, myPosition.getColumn() - 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow(), myPosition.getColumn() + 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow(), myPosition.getColumn() - 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() + 1, myPosition.getColumn());
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() - 1, myPosition.getColumn());
        return  moves;
    }

    private HashSet<ChessMove> knightRules(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() + 1, myPosition.getColumn() + 2);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() + 1, myPosition.getColumn() - 2);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() - 1, myPosition.getColumn() + 2);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() - 1, myPosition.getColumn() - 2);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() + 2, myPosition.getColumn() + 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() + 2, myPosition.getColumn() - 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() - 2, myPosition.getColumn() + 1);
        addMoveIfValid(board,myPosition,moves,myPosition.getRow() - 2, myPosition.getColumn() - 1);
        return  moves;
    }

    private HashSet<ChessMove> pawnRules(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        ChessPiece current_piece = board.getPiece(myPosition);
        int direction;
        if (current_piece.getTeamColor() == ChessGame.TeamColor.BLACK) { direction = -1; } else { direction = 1; }
        addMoveIfValidPawn(board, myPosition, moves, myPosition.getRow() + direction, myPosition.getColumn(), false);
        if ((current_piece.getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) || (current_piece.getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2)){
            if (board.getPiece(new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn())) == null) {
                addMoveIfValidPawn(board, myPosition, moves, myPosition.getRow() + direction * 2, myPosition.getColumn(), true);
            }
        }
        return  moves;
    }

    void getDiagonals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        movesInLine(board,myPosition,moves,1,1);
        movesInLine(board,myPosition,moves,1,-1);
        movesInLine(board,myPosition,moves,-1,1);
        movesInLine(board,myPosition,moves,-1,-1);
    }

    void getHorizontals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        movesInLine(board,myPosition,moves,1,0);
        movesInLine(board,myPosition,moves,-1,0);
        movesInLine(board,myPosition,moves,0,1);
        movesInLine(board,myPosition,moves,0,-1);
    }


    void movesInLine(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int xDir, int yDir) {
        ChessPiece current_piece = board.getPiece(myPosition);
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        if (xDir == 1) { x++; } else if (xDir == -1) { x--; }
        if (yDir == 1) { y++; } else if (yDir == -1) { y--; }
        while ((x <=8 && x >= 1) && (y <=8 && y >= 1)) {
            ChessPosition new_position = new ChessPosition(x, y);
            ChessPiece piece_at_position = board.getPiece(new_position);
            if (piece_at_position == null) {
                moves.add(new ChessMove(myPosition, new_position, null));
            } else {
                if (piece_at_position.getTeamColor() != current_piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, new_position, null));
                }
                break;
            }
            if (xDir == 1) { x++; } else if (xDir == -1) { x--; }
            if (yDir == 1) { y++; } else if (yDir == -1) { y--; }
        }
    }


    void addMoveIfValid(ChessBoard board, ChessPosition myPosition,  HashSet<ChessMove> moves, int x, int y) {
        ChessPiece current_piece = board.getPiece(myPosition);
        if ((x <=8 && x >= 1) && (y <=8 && y >= 1)) {
            ChessPosition new_position = new ChessPosition(x, y);
            ChessPiece piece_at_position = board.getPiece(new_position);
            if (piece_at_position == null) {
                moves.add(new ChessMove(myPosition, new_position, null));
            } else if (piece_at_position.getTeamColor() != current_piece.getTeamColor()) {
                moves.add(new ChessMove(myPosition, new_position, null));
            }
        }
    }


    void addMoveIfValidPawn(ChessBoard board, ChessPosition myPosition,  HashSet<ChessMove> moves, int x, int y, boolean first_move) {
        ChessPiece current_piece = board.getPiece(myPosition);
        if ((x <=8 && x >= 1) && (y <=8 && y >= 1)) {
            ChessPosition new_position = new ChessPosition(x, y);
            ChessPiece piece_at_position = board.getPiece(new_position);
            if (piece_at_position == null) {
                addPawnPiece(myPosition, new_position, current_piece,moves);
            }
            if (!first_move) {
                if (y != 8) {
                    new_position = new ChessPosition(x, y + 1);
                    piece_at_position = board.getPiece(new_position);
                    if (piece_at_position != null && piece_at_position.getTeamColor() != current_piece.getTeamColor()) {
                        addPawnPiece(myPosition, new_position, current_piece, moves);
                    }
                }
                if (y != 1) {
                    new_position = new ChessPosition(x, y - 1);
                    piece_at_position = board.getPiece(new_position);
                    if (piece_at_position != null && piece_at_position.getTeamColor() != current_piece.getTeamColor()) {
                        addPawnPiece(myPosition, new_position, current_piece, moves);
                    }
                }
            }
        }
    }

    void addPawnPiece(ChessPosition myPosition, ChessPosition new_position, ChessPiece current_piece, HashSet<ChessMove> moves) {
        if ((new_position.getRow() == 8 && current_piece.getTeamColor() == ChessGame.TeamColor.WHITE) || (new_position.getRow() == 1 && current_piece.getTeamColor() == ChessGame.TeamColor.BLACK)) {
            moves.add(new ChessMove(myPosition, new_position, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, new_position, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, new_position, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, new_position, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(myPosition, new_position, null));
        }
    }


    @Override
    public String toString() {
        return pieceColor + "." + type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
