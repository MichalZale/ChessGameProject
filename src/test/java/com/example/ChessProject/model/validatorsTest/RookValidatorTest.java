package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.RookValidator;
import static com.example.ChessProject.model.BoardBuilder.*;

class RookValidatorTest {

    @Test void horizontalCapture(){
        Board b = new BoardBuilder()
                  .add( wr(4,0) ).add( bp(4,5) )
                  .add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(4,0),p(4,5));
        assertTrue(new RookValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }

    @Test void verticalBlocked(){
        Board b = new BoardBuilder()
                  .add( wr(4,0) ).add( wp(5,0) )
                  .add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(4,0),p(6,0));
        assertFalse(new RookValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }
}
