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
        Move mv = new Move(p(3,4),p(2,5));
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
