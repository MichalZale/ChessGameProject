package com.example.ChessProject.model.validators;

import com.example.ChessProject.model.*;
import com.example.ChessProject.model.pieces.Bishop;
import com.example.ChessProject.model.pieces.Knight;
import com.example.ChessProject.model.pieces.Pawn;
import com.example.ChessProject.model.pieces.Queen;
import com.example.ChessProject.model.pieces.Rook;

public class PawnValidator extends MoveValidator {
    public PawnValidator(GameState s) {
        super(s);
    }

    @Override
    public boolean basicMove(Move move, GameState st) {
        Position from = move.getFrom(), to = move.getTo();
        Board b = st.getBoard();

        if (!b.isInside(from) || !b.isInside(to))
            return false;
        Piece p = b.getPiece(from);

        if (!(p instanceof Pawn)) 
            return false; 
        if (p.getColor() != st.getSideToMove())
            return false;
        
        Piece pieceAtTarget = b.getPiece(to);
        if (pieceAtTarget != null && pieceAtTarget.getColor() == p.getColor())
            return false;

        int dir = (p.getColor() == Color.WHITE) ? -1 : 1;
        int startRow = (p.getColor() == Color.WHITE) ? 6 : 1;
        int promotionRank = (p.getColor() == Color.WHITE) ? 0 : 7;

        int dr = to.row - from.row;
        int dc = Math.abs(to.column - from.column);

        if (dc == 0) { // Moving straight
            // Single push
            if (dr == dir && b.isEmpty(to)) {
                if (to.row == promotionRank) {
                    // Must be a promotion move with a valid promotion piece
                    return move.getMoveType() == Move.MoveType.PROMOTION 
                           && move.getPromotionTo() != null
                           && (move.getPromotionTo() instanceof Queen || move.getPromotionTo() instanceof Rook ||
                               move.getPromotionTo() instanceof Bishop || move.getPromotionTo() instanceof Knight);
                } else {
                    // Normal single push
                    return move.getMoveType() == Move.MoveType.NORMAL;
                }
            }
            // Double push
            if (dr == 2 * dir && from.row == startRow && b.isEmpty(to)
                    && b.isEmpty(new Position(from.row + dir, from.column))) {
                return move.getMoveType() == Move.MoveType.PAWN_DOUBLE_PUSH;
            }
        }
        // Captures (dc == 1)
        if (dc == 1 && dr == dir) {
            // Normal capture
            if (pieceAtTarget != null && pieceAtTarget.getColor() != p.getColor()) {
                if (to.row == promotionRank) {
                    // Must be a promotion move with a valid promotion piece
                    return move.getMoveType() == Move.MoveType.PROMOTION 
                           && move.getPromotionTo() != null
                           && (move.getPromotionTo() instanceof Queen || move.getPromotionTo() instanceof Rook ||
                               move.getPromotionTo() instanceof Bishop || move.getPromotionTo() instanceof Knight);
                } else {
                    // Normal capture
                    return move.getMoveType() == Move.MoveType.NORMAL;
                }
            }
            
            if (pieceAtTarget == null && st.getEnPassant() != null && st.getEnPassant().equals(to)) { 
                return move.getMoveType() == Move.MoveType.EN_PASSANT;
            }
        }
        return false;
    }

    @Override
    public boolean canAttack(Position from, Position target, Board b) {
        Color col = b.getPiece(from).getColor();
        int dir = (col == Color.WHITE) ? -1 : 1;
        int dr = target.row - from.row;
        int dc = Math.abs(target.column - from.column);
        return dr == dir && dc == 1;
    }
}
