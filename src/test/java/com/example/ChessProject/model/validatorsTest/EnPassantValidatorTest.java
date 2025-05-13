package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.*;
import com.example.ChessProject.model.pieces.*;
import static com.example.ChessProject.model.BoardBuilder.*;

public class EnPassantValidatorTest {

    @Test void enPassantWhite(){
        Board b = new BoardBuilder()
                .add(wp(3,4))
                .add(bp(3,5))
                .add(wK(1,7))
                .add(bK(6, 3))
                .build();
        GameState gs = new GameState(b, Color.WHITE,true,true,true,true,p(2,5));
        Pawn w = (Pawn)b.getPiece(p(3,4));
        
        Move mv = new Move(p(3,4),p(2,5), Move.MoveType.EN_PASSANT);
        assertTrue(new PawnValidator(gs).isValidMove(mv));
    }

    @Test
    void enPassantBlack() {
        Board b = new BoardBuilder()
                .add(bp(4, 4)) // Black pawn at e4
                .add(wp(4, 3)) // White pawn at d4
                .add(bK(0, 7)).add(wK(7, 0))
                .build();
        // Black to move, white pawn at (4,3) just moved from (6,3), so en passant target is (5,3) (d3)
        GameState gs = new GameState(b, Color.BLACK, true, true, true, true, p(5, 3));
        Move mv = new Move(p(4, 4), p(5, 3), Move.MoveType.EN_PASSANT); // Black pawn e4xd3 e.p.
        assertTrue(new PawnValidator(gs).isValidMove(mv));
    }

    @Test void enPassantIllegalAfterOneMove() {
        Board b = new BoardBuilder()
                .add( wp(3,4) ) 
                .add( bp(3,5) )   
                .add( wK(1,7) ).add( bK(6,3) )
                .build();
    
        GameState gs = new GameState(b, Color.WHITE,
                                     true,true,true,true,
                                     null);            
    
        Move mv = new Move( p(3,4), p(2,5) );         
        assertFalse( new PawnValidator(gs).isValidMove(mv) );
    }
    
}
