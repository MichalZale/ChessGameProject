package com.example.ChessProject.model;
import com.example.ChessProject.model.pieces.*;
import com.example.ChessProject.model.validators.*;
public final class MoveValidatorFactory {
    public static MoveValidator getValidator(Piece piece, GameState s) {
        if (piece instanceof Pawn)   return new PawnValidator(s);
        if (piece instanceof Knight) return new KnightValidator(s);
        if (piece instanceof Bishop) return new BishopValidator(s);
        if (piece instanceof Rook)   return new RookValidator(s);
        if (piece instanceof Queen)  return new QueenValidator(s);
        if (piece instanceof King)   return new KingValidator(s);
        throw new IllegalArgumentException("Unknown piece class "+piece.getClass());
    }
}