package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.QueenValidator;
import static com.example.ChessProject.model.BoardBuilder.*;

class QueenValidatorTest {

    @Test void diagLegal(){
        Board b = new BoardBuilder()
                  .add( wq(3,3) ).add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(3,3),p(1,1));
        assertTrue(new QueenValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }

    @Test void straightBlocked(){
        Board b = new BoardBuilder()
                  .add( wq(3,3) ).add( wp(3,5) )
                  .add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(3,3),p(3,7));
        assertFalse(new QueenValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }
}
