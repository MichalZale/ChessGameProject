package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.KingValidator;
import static com.example.ChessProject.model.BoardBuilder.*;

class CastlingValidatorTest {

    @Test void longCastleBlockedByPiece(){
        Board b = new BoardBuilder()
                  .add( wK(7,4) ).add( wr(7,0) )
                  .add( wp(7,2) )  .add( bK(0,4) )
                  .build();
        Move mv = new Move(p(7,4),p(7,2));
        assertFalse(new KingValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }
}