package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.validators.*;
import static com.example.ChessProject.model.BoardBuilder.*;

public class KnightValidatorTest {

    GameState gs(Board b){ return new GameState(b, Color.WHITE,true,true,true,true,null); }

    @Test void legalL(){
        Board b = new BoardBuilder()
             .add( wk(4,4) )          // white knight
             .add( wK(7,4) )          // white king
             .add( bK(0,4) )          //black king
             .build();

        Move mv = new Move(p(4,4),p(6,5));
        assertTrue(new KnightValidator(gs(b)).isValidMove(mv));
    }

    @Test void blockedByOwn(){
        Board b = new BoardBuilder()
            .add(wk(4,4))
            .add(wp(6,5))
            .add(wK(7,4))
            .add(bK(0,4))
            .build();
        Move mv = new Move(p(4,4),p(6,5));
        assertFalse(new KnightValidator(gs(b)).isValidMove(mv));
    }

    @Test void captureEnemy(){
        Board b = new BoardBuilder()
            .add(wk(4,4))
            .add(bp(6,5))
            .add(wK(7,4))
            .add(bK(0,4))
            .build();
        Move mv = new Move(p(4,4),p(6,5));
        assertTrue(new KnightValidator(gs(b)).isValidMove(mv));
    }

    @Test void illegalGeometry(){
        Board b = new BoardBuilder()
            .add(wk(4,4))
            .add(wK(7,4))
            .add(bK(0,4))
            .build();
        Move mv = new Move(p(4,4),p(5,5));
        assertFalse(new KnightValidator(gs(b)).isValidMove(mv));
    }
}
