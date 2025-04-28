package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.PawnValidator;
import static com.example.ChessProject.model.BoardBuilder.*;

class PawnValidatorTest {

    @Test void singlePush(){
        Board b = new BoardBuilder()
                  .add( wp(4,4) ).add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(4,4),p(3,4));
        assertTrue(new PawnValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }

    @Test void doublePushBlocked(){
        Board b = new BoardBuilder()
                  .add( wp(6,4) ).add( bp(5,4) )
                  .add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(6,4),p(4,4));
        assertFalse(new PawnValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }

    @Test void enPassant(){
        Board b = new BoardBuilder()
                  .add( wp(3,4) ).add( bp(3,5) )
                  .add( wK(7,4) ).add( bK(0,4) )
                  .build();
        GameState gs = new GameState(b,Color.WHITE,true,true,true,true,p(2,5));
        Move mv = new Move(p(3,4),p(2,5));
        assertTrue(new PawnValidator(gs).isValidMove(mv));
    }
}
