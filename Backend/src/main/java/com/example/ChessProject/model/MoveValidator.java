package com.example.ChessProject.model;

import java.util.ArrayList;
import java.util.List;
import com.example.ChessProject.model.pieces.*;

public abstract class MoveValidator {
    protected final GameState state;
    protected final Board board;
    protected final Color turn;

    public MoveValidator(GameState s) {
        this.state = s;
        this.board = s.getBoard();
        this.turn = s.getSideToMove();
    }

    public boolean isValidMove(Move mv) {
        if (!basicMove(mv, this.state))
            return false;

        Piece moving = board.getPiece(mv.getFrom());
        boolean castling = moving instanceof King &&
                Math.abs(mv.getFrom().column - mv.getTo().column) == 2;

        if (castling) {
            if (isInCheck(state, turn))
                return false;
            if (!isCastlingLineSafe(mv.getFrom(), mv.getTo(), state))
                return false;
        }

        GameState sim = simulateMove(mv);
        return !isInCheck(sim, turn);
    }

    protected abstract boolean basicMove(Move mv, GameState state);

    protected abstract boolean canAttack(Position from, Position target, Board b);

    public boolean isInCheck(GameState gs, Color kingColor) {
        Position kingPos = findKing(kingColor);
        Color attacker = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return isSquareAttacked(kingPos, attacker, gs);
    }

    protected List<Move> generateAllLegalMoves() {
        List<Move> allLegalMoves = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece p = board.getPiece(from);
                if (p != null && p.getColor() == turn) {
                    MoveValidator v = MoveValidatorFactory.getValidator(p, state);
                    for (Position to : p.getPseudoMoves(this.state)) {
                        Move mv = new Move(from, to);
                        if (v.isValidMove(mv))
                            allLegalMoves.add(mv);
                    }
                }
            }
        return allLegalMoves;
    }

    protected boolean isCheckmate() {
        return isInCheck(state, turn) && generateAllLegalMoves().isEmpty();
    }

    protected boolean isStalemate() {
        return !isInCheck(state, turn) && generateAllLegalMoves().isEmpty();
    }

    protected boolean escapesCheck(Move mv) {
        return !isInCheck(simulateMove(mv), turn);
    }

    public GameState simulateMove(Move mv) {
        GameState sim = state.clone();
        Board nb = sim.getBoard(); 

        Position from = mv.getFrom();
        Position to = mv.getTo();

        Piece movingPieceOriginal = state.getBoard().getPiece(from);

        if (movingPieceOriginal == null) {
            throw new IllegalStateException("Cannot simulate move from an empty square: " + from);
        }

        if (mv.getMoveType() == Move.MoveType.PROMOTION && movingPieceOriginal instanceof Pawn) {
            Piece promotedPiece = mv.getPromotionTo();
            if (promotedPiece == null) {
                throw new IllegalStateException("Promotion move type without a promotion piece.");
            }
            Piece newPiece = promotedPiece.clone(); 
            newPiece.setPosition(to); 
            if (newPiece.getColor() != movingPieceOriginal.getColor()) {
                throw new IllegalStateException("Promoted piece color does not match pawn color.");
            }

            nb.setPiece(to, newPiece); 
            nb.setPiece(from, null); 

            if (newPiece instanceof Rook)
                ((Rook) newPiece).setHasMoved(true);
            

        } else { 
            Piece pieceToPlace = movingPieceOriginal.clone();
            pieceToPlace.setPosition(to);
            nb.setPiece(to, pieceToPlace);
            nb.setPiece(from, null);

            if (pieceToPlace instanceof Pawn)
                ((Pawn) pieceToPlace).setHasMoved(true);
            else if (pieceToPlace instanceof King)
                ((King) pieceToPlace).setHasMoved(true);
            else if (pieceToPlace instanceof Rook)
                ((Rook) pieceToPlace).setHasMoved(true);
        }

        if (mv.getMoveType() == Move.MoveType.EN_PASSANT && movingPieceOriginal instanceof Pawn) {
            int capturedPawnRow = from.row;
          
            nb.setPiece(new Position(capturedPawnRow, to.column), null);
        }

        if ((mv.getMoveType() == Move.MoveType.CASTLE_KINGSIDE || mv.getMoveType() == Move.MoveType.CASTLE_QUEENSIDE)
                && movingPieceOriginal instanceof King) {
            boolean kSide = mv.getMoveType() == Move.MoveType.CASTLE_KINGSIDE;
            Position rookFrom = new Position(from.row, kSide ? 7 : 0);
            Position rookTo = new Position(from.row, kSide ? 5 : 3);

            Piece originalRook = state.getBoard().getPiece(rookFrom); 
            if (originalRook instanceof Rook) {
                Piece rookClone = originalRook.clone();
                rookClone.setPosition(rookTo);
                ((Rook) rookClone).setHasMoved(true); 
                nb.setPiece(rookTo, rookClone);
                nb.setPiece(rookFrom, null);
            } else {
                throw new IllegalStateException("Rook not found for castling at " + rookFrom);
            }
        }

        if (movingPieceOriginal instanceof King && mv.getMoveType() != Move.MoveType.PROMOTION) {
            if (movingPieceOriginal.getColor() == Color.WHITE) {
                sim.setWCastleK(false);
                sim.setWCastleQ(false);
            } else {
                sim.setBCastleK(false);
                sim.setBCastleQ(false);
            }
        }
        if (movingPieceOriginal instanceof Rook && mv.getMoveType() != Move.MoveType.PROMOTION) {
            if (movingPieceOriginal.getColor() == Color.WHITE) {
                if (from.equals(new Position(7, 0)))
                    sim.setWCastleQ(false);
                if (from.equals(new Position(7, 7)))
                    sim.setWCastleK(false);
            } else {
                if (from.equals(new Position(0, 0)))
                    sim.setBCastleQ(false);
                if (from.equals(new Position(0, 7)))
                    sim.setBCastleK(false);
            }
        }
        Piece capturedPiece = state.getBoard().getPiece(to); 
        if (capturedPiece instanceof Rook) {
            if (capturedPiece.getColor() == Color.WHITE) {
                if (to.equals(new Position(7, 0)))
                    sim.setWCastleQ(false);
                if (to.equals(new Position(7, 7)))
                    sim.setWCastleK(false);
            } else {
                if (to.equals(new Position(0, 0)))
                    sim.setBCastleQ(false);
                if (to.equals(new Position(0, 7)))
                    sim.setBCastleK(false);
            }
        }

        if (mv.getMoveType() == Move.MoveType.PAWN_DOUBLE_PUSH && movingPieceOriginal instanceof Pawn) {
            int midRow = (from.row + to.row) / 2;
            sim.setEnPassant(new Position(midRow, from.column));
        } else {
            sim.setEnPassant(null); 
        }

        sim.switchSideToMove();
        return sim;
    }

    protected Position findKing(Color c) {
        for (int r = 0; r < 8; r++)
            for (int col = 0; col < 8; col++) {
                Position p = new Position(r, col);
                Piece pc = board.getPiece(p);
                if (pc instanceof King && pc.getColor() == c)
                    return p;
            }
        ;
        throw new IllegalStateException("King not found");
    }

    protected boolean isCastlingLineSafe(Position kFrom, Position kTo, GameState st) {
        Color opp = (st.getSideToMove() == Color.WHITE) ? Color.BLACK : Color.WHITE;
        int row = kFrom.row;
        int step = (kFrom.column < kTo.column) ? 1 : -1;
        Position mid = new Position(row, kFrom.column + step);
        if (isSquareAttacked(mid, opp, st))
            return false;
        return !isSquareAttacked(kTo, opp, st);
    }

    protected boolean isSquareAttacked(Position sq, Color attacker, GameState gs) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece p = gs.getBoard().getPiece(from);
                if (p != null && p.getColor() == attacker) {
                    MoveValidator v = MoveValidatorFactory.getValidator(p, gs);
                    if (v.canAttack(from, sq, gs.getBoard()))
                        return true;
                }
            }
        return false;
    }

    /*
     * public List<Position> getPossibleMoves(Position from, GameState state){
     * List<Position> moves = new ArrayList<>();
     * Piece piece = state.getBoard().getPiece(from);
     * for(Position to : piece.getPseudoMoves(state)){
     * Move mv = new Move(from, to);
     * if(isValidMove(mv));
     * moves.add(to);
     * }
     * return moves;
     * };
     */
}
