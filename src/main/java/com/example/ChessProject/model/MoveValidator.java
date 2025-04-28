package com.example.ChessProject.model;

import java.util.ArrayList;
import java.util.List;
import com.example.ChessProject.model.pieces.*;

public abstract class MoveValidator {
    protected final GameState state;
    protected final Board     board;
    protected final Color     turn;

    public MoveValidator(GameState s) {
        this.state = s;
        this.board = s.getBoard();
        this.turn  = s.getSideToMove();
    }

    public boolean isValidMove(Move mv) {
        if (!basicMove(mv, this.state)) return false;

        Piece moving = board.getPiece(mv.getFrom());
        boolean castling = moving instanceof King &&
                           Math.abs(mv.getFrom().column - mv.getTo().column) == 2;

        if (castling) {
            if (isInCheck(state, turn)) return false;
            if (!isCastlingLineSafe(mv.getFrom(), mv.getTo(), state)) return false;
        }

        GameState sim = simulateMove(mv);
        return !isInCheck(sim, turn);
    }
 
    protected abstract boolean basicMove(Move mv, GameState state);

    protected abstract boolean canAttack(Position from, Position target, Board b);

    protected boolean isInCheck(GameState gs, Color kingColor) {
        Position kingPos = findKing(kingColor);
        Color attacker = (kingColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
        return isSquareAttacked(kingPos, attacker, gs);
    }


    protected List<Move> generateMoves() {
        List<Move> list = new ArrayList<>();
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece p = board.getPiece(from);
                if (p != null && p.getColor() == turn) {
                    MoveValidator v = MoveValidatorFactory.getValidator(p, state);
                    for (Position to : p.getPseudoMoves(this.state)) {
                        Move mv = new Move(from, to);
                        if (v.isValidMove(mv)) list.add(mv);
                    }
                }
            }
        return list;
    }

    private boolean isCheckmate() {
        return isInCheck(state, turn) && generateMoves().isEmpty();
    }

    private boolean isStelmate() {
        return !isInCheck(state, turn) && generateMoves().isEmpty();
    }

    protected boolean escapesCheck(Move mv) {
        return !isInCheck(simulateMove(mv), turn);
    }

    protected GameState simulateMove(Move mv) {

        GameState sim = state.clone();
        Board nb = sim.getBoard();

        Position from = mv.getFrom();
        Position to   = mv.getTo();

        Piece moving = nb.getPiece(from);
        nb.setPiece(to, moving);
        nb.setPiece(from, null);

        Position ep = state.getEnPassant();

        /* en-passant */
        if (moving instanceof Pawn && ep != null &&
            ep.equals(to) && from.column != to.column) {
            int capRow = to.row + (turn == Color.WHITE ? 1 : -1);
            nb.setPiece(new Position(capRow, to.column), null);
        }

        /* roszada */
        if (moving instanceof King && Math.abs(from.column - to.column) == 2) {
            boolean kSide = to.column == 6;
            Position rookFrom = new Position(from.row, kSide ? 7 : 0);
            Position rookTo   = new Position(from.row, kSide ? 5 : 3);
            nb.setPiece(rookTo, nb.getPiece(rookFrom));
            nb.setPiece(rookFrom, null);
            if (turn == Color.WHITE) {
                sim.setWCastleK(false); sim.setWCastleQ(false);
            } else {
                sim.setBCastleK(false); sim.setBCastleQ(false);
            }
        }

        /* prawa roszady gdy ruszy wieża/król */
        if (moving instanceof Rook) {
            if (turn == Color.WHITE && from.equals(new Position(7,0))) sim.setWCastleQ(false);
            if (turn == Color.WHITE && from.equals(new Position(7,7))) sim.setWCastleK(false);
            if (turn == Color.BLACK && from.equals(new Position(0,0))) sim.setBCastleQ(false);
            if (turn == Color.BLACK && from.equals(new Position(0,7))) sim.setBCastleK(false);
        }
        if (moving instanceof King) {
            if (turn == Color.WHITE) { sim.setWCastleK(false); sim.setWCastleQ(false); }
            else                     { sim.setBCastleK(false); sim.setBCastleQ(false); }
        }

        /* nowe en-passant */
        if (moving instanceof Pawn && Math.abs(from.row - to.row) == 2) {
            int mid = (from.row + to.row) / 2;
            sim.setEnPassant(new Position(mid, from.column));
        } else sim.setEnPassant(null);

        sim.switchSideToMove();
        return sim;
    }

    protected Position findKing(Color c) {
        for (int r = 0; r < 8; r++)
            for (int col = 0; col < 8; col++) {
                Position p = new Position(r, col);
                Piece pc = board.getPiece(p);
                if (pc instanceof King && pc.getColor() == c) return p;
            }
        ;throw new IllegalStateException("King not found");
    }

    protected boolean isCastlingLineSafe(Position kFrom, Position kTo, GameState st) {
        Color opp = (st.getSideToMove() == Color.WHITE) ? Color.BLACK : Color.WHITE;
        int row = kFrom.row;
        int step = (kFrom.column < kTo.column) ? 1 : -1;
        Position mid = new Position(row, kFrom.column + step);
        if (isSquareAttacked(mid, opp, st)) return false;
        return !isSquareAttacked(kTo, opp, st);
    }

    protected boolean isSquareAttacked(Position sq, Color attacker, GameState gs) {
        for (int r = 0; r < 8; r++)
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                Piece p = gs.getBoard().getPiece(from);
                if (p != null && p.getColor() == attacker) {
                    MoveValidator v = MoveValidatorFactory.getValidator(p, gs);
                    if (v.canAttack(from, sq, gs.getBoard())) return true;
                }
            }
        return false;
    }

    public List<Position> getPossibleMoves(Position from, GameState state){
        List<Position> moves = new ArrayList<>();
        Piece piece = state.getBoard().getPiece(from);
        for(Position to : piece.getPseudoMoves(state)){
            Move mv = new Move(from, to);
            if(isValidMove(mv));
                moves.add(to);
        }
        return moves;
    };
}
