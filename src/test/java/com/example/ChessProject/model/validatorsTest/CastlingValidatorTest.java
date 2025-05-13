package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.KingValidator;
import static com.example.ChessProject.model.BoardBuilder.*;

class CastlingValidatorTest {
    @Test
    void whiteKingsideCastleLegal() {
        Board b = new BoardBuilder()
                .add(wK(7, 4)).add(wr(7, 7)) // White King e1, Rook h1
                .add(bK(0, 4))
                .build();
        GameState gs = new GameState(b, Color.WHITE, true, true, true, true, null);
        Move mv = new Move(p(7, 4), p(7, 6), Move.MoveType.CASTLE_KINGSIDE);
        assertTrue(new KingValidator(gs).isValidMove(mv));
    }

    @Test
    void blackQueensideCastleLegal() {
        Board b = new BoardBuilder()
                .add(bK(0, 4)).add(br(0, 0)) // Black King e8, Rook a8
                .add(wK(7, 4))
                .build();
        GameState gs = new GameState(b, Color.BLACK, true, true, true, true, null);
        Move mv = new Move(p(0, 4), p(0, 2), Move.MoveType.CASTLE_QUEENSIDE);
        assertTrue(new KingValidator(gs).isValidMove(mv));
    }

    @Test
    void whiteKingsideCastle_KingHasMoved() {
        Board b = new BoardBuilder()
                .add(wK(7, 4)).add(wr(7, 7))
                .add(bK(0, 4))
                .build();
        // King has moved (even if back to original square, castling rights are lost)
        // For this test, we directly set castling rights to false.
        // A more complex test would simulate a king move first.
        GameState gs = new GameState(b, Color.WHITE, false, true, true, true, null); // wCK is false
        Move mv = new Move(p(7, 4), p(7, 6), Move.MoveType.CASTLE_KINGSIDE);
        assertFalse(new KingValidator(gs).isValidMove(mv));
    }

    @Test
    void whiteKingsideCastle_RookHasMoved() {
        Board b = new BoardBuilder()
                .add(wK(7, 4)).add(wr(7, 7))
                .add(bK(0, 4))
                .build();
        // Rook has moved
        GameState gs = new GameState(b, Color.WHITE, false, true, true, true, null); // wCK is false
        // To properly test this, the Rook piece itself should have its 'hasMoved' flag set,
        // and GameState castling flags updated accordingly after a rook move.
        // This test simplifies by directly setting GameState.wCastleK to false.
        Move mv = new Move(p(7, 4), p(7, 6), Move.MoveType.CASTLE_KINGSIDE);
        assertFalse(new KingValidator(gs).isValidMove(mv));
    }

    @Test void longCastleBlockedByPiece(){
        Board b = new BoardBuilder()
                  .add( wK(7,4) ).add( wr(7,0) )
                  .add( wp(7,2) )  .add( bK(0,4) )
                  .build();
        Move mv = new Move(p(7,4),p(7,2));
        assertFalse(new KingValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }
}