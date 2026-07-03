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

        // LOGI DEBUGUJĄCE:
        System.out.println("[PAWN VALIDATOR] --- SPRAWDZAM RUCH ---");
        System.out.println("[PAWN VALIDATOR] from: " + from + ", to: " + to);
        System.out.println("[PAWN VALIDATOR] moveType: " + move.getMoveType());
        Piece p = b.getPiece(from);
        System.out.println("[PAWN VALIDATOR] Piece at FROM: " + p);
        if (p != null) {
            System.out.println("[PAWN VALIDATOR] Piece color: " + p.getColor());
        }
        System.out.println("[PAWN VALIDATOR] sideToMove: " + st.getSideToMove());
        Piece pieceAtTarget = b.getPiece(to);
        System.out.println("[PAWN VALIDATOR] Piece at TO: " + pieceAtTarget);
        System.out.println("[PAWN VALIDATOR] Board.isEmpty(to): " + b.isEmpty(to));

        // Dalej Twój kod:
        if (!b.isInside(from) || !b.isInside(to))
            return false;
        if (!(p instanceof Pawn))
            return false;
        if (p.getColor() != st.getSideToMove())
            return false;
        if (pieceAtTarget != null && pieceAtTarget.getColor() == p.getColor())
            return false;

        int dir = (p.getColor() == Color.WHITE) ? -1 : 1;
        int startRow = (p.getColor() == Color.WHITE) ? 6 : 1; 
        int promotionRank = (p.getColor() == Color.WHITE) ? 0 : 7;

        int dr = to.row - from.row;
        int dc = Math.abs(to.column - from.column);

        System.out.println("[PAWN VALIDATOR] dr: " + dr + ", dc: " + dc + ", dir: " + dir + ", startRow: " + startRow
                + ", promotionRank: " + promotionRank);

        if (dc == 0) { // Moving straight
            if (dr == dir && b.isEmpty(to)) {
                if (to.row == promotionRank) {
                    System.out.println("[PAWN VALIDATOR] -> próba promocji");
                    return move.getMoveType() == Move.MoveType.PROMOTION
                            && move.getPromotionTo() != null
                            && (move.getPromotionTo() instanceof Queen || move.getPromotionTo() instanceof Rook ||
                                    move.getPromotionTo() instanceof Bishop || move.getPromotionTo() instanceof Knight);
                } else {
                    System.out
                            .println("[PAWN VALIDATOR] -> pojedynczy ruch do przodu, moveType==" + move.getMoveType());
                    return move.getMoveType() == Move.MoveType.NORMAL;
                }
            }
            if (dr == 2 * dir && from.row == startRow && b.isEmpty(to)
                    && b.isEmpty(new Position(from.row + dir, from.column))) {
                System.out.println("[PAWN VALIDATOR] -> podwójny ruch do przodu, moveType==" + move.getMoveType());
                return move.getMoveType() == Move.MoveType.PAWN_DOUBLE_PUSH;
            }
        }
        if (dc == 1 && dr == dir) {
            if (pieceAtTarget != null && pieceAtTarget.getColor() != p.getColor()) {
                if (to.row == promotionRank) {
                    System.out.println("[PAWN VALIDATOR] -> bicie z promocją");
                    return move.getMoveType() == Move.MoveType.PROMOTION
                            && move.getPromotionTo() != null
                            && (move.getPromotionTo() instanceof Queen || move.getPromotionTo() instanceof Rook ||
                                    move.getPromotionTo() instanceof Bishop || move.getPromotionTo() instanceof Knight);
                } else {
                    System.out.println("[PAWN VALIDATOR] -> normalne bicie");
                    return move.getMoveType() == Move.MoveType.NORMAL;
                }
            }
            if (pieceAtTarget == null && st.getEnPassant() != null && st.getEnPassant().equals(to)) {
                System.out.println("[PAWN VALIDATOR] -> en passant!");
                return move.getMoveType() == Move.MoveType.EN_PASSANT;
            }
        }
        System.out.println("[PAWN VALIDATOR] -> ruch nie został zaakceptowany");
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
