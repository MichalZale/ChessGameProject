package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.BishopValidator;
import static com.example.ChessProject.model.BoardBuilder.*;

class PinnedPieceValidatorTest {

    @Test void bishopPinnedByRook(){
        Board b = new BoardBuilder()
                  .add( wK(7,4) ).add( bK(0,4) )
                  .add( wb(6,4) ).add( br(0,4) )
                  .build();
        Move mv = new Move(p(6,4),p(5,5));
        assertFalse(new BishopValidator(gs(b,Color.WHITE)).isValidMove(mv));
    }
}
