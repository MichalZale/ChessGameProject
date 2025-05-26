package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.KingValidator;
import static com.example.ChessProject.model.BoardBuilder.*;

class KingValidatorTest {

    @Test void normalMove(){
        Board b = new BoardBuilder()
                  .add( wK(7,4) ).add( bK(0,4) )
                  .build();
        Move mv = new Move(p(7,4),p(6,4));
        assertTrue(new KingValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }

    @Test void shortCastleLegal(){
        Board b = new BoardBuilder()
                  .add( wK(7,4) ).add( wr(7,7) ) .add( bK(0,4) )
                  .build();
        Move mv = new Move(p(7,4),p(7,6));
        assertTrue(new KingValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }

    @Test void castleThroughAttackIllegal(){
        Board b = new BoardBuilder()
                  .add( wK(7,4) ).add( wr(7,7) )
                  .add( br(5,5) ) .add( bK(0,4) )
                  .build();
        Move mv = new Move(p(7,4),p(7,6));
        assertFalse(new KingValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }
}
