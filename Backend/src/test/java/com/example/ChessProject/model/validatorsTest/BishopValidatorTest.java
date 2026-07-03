package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.BishopValidator;
import static com.example.ChessProject.model.BoardBuilder.*;

class BishopValidatorTest {

    @Test void diagonalMove(){
        Board b = new BoardBuilder()
                  .add( wb(4,4) ).add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(4,4),p(1,1));
        assertTrue(new BishopValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }

    @Test void pathBlocked(){
        Board b = new BoardBuilder()
                  .add( wb(4,4) ).add( wp(3,3) )
                  .add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(4,4),p(1,1));
        assertFalse(new BishopValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }
}
