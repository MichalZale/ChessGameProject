package com.example.ChessProject.model.validatorsTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.pieces.Knight;
import com.example.ChessProject.model.pieces.Pawn;
import com.example.ChessProject.model.pieces.Queen;
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
        Move mv = new Move(p(3,4),p(2,5), Move.MoveType.EN_PASSANT);
        assertTrue(new PawnValidator(gs).isValidMove(mv));
    }

    @Test
    void whitePawnPromotesToQueen_NormalMove() {
        Board b = new BoardBuilder()
                .add(wp(1, 0)) 
                .add(wK(7, 4)).add(bK(2, 4))
                .build();
        GameState gs = gs(b, Color.WHITE);
        Move mv = new Move(p(1, 0), p(0, 0), new Queen(Color.WHITE, p(0,0)), Move.MoveType.PROMOTION);
        assertTrue(new PawnValidator(gs).isValidMove(mv));
    }

    @Test
    void blackPawnPromotesToKnight_CaptureMove() {
        Board b = new BoardBuilder()
                .add(bp(6, 3))
                .add(wr(7, 2)) 
                .add(bK(0, 4)).add(wK(5, 4))
                .build();
        GameState gs = gs(b, Color.BLACK);
        Move mv = new Move(p(6, 3), p(7, 2), new Knight(Color.BLACK, p(7,2)), Move.MoveType.PROMOTION);
        assertTrue(new PawnValidator(gs).isValidMove(mv));
    }

    @Test
    void whitePawnPromotesToQueen_DeliversCheck() {
        Board b = new BoardBuilder()
                .add(wp(1, 0)) 
                .add(wK(2, 2)) 
                .add(bK(0, 1))
                .build();
        GameState gs = gs(b, Color.WHITE);
        Move mv = new Move(p(1, 0), p(0, 0), new Queen(Color.WHITE, p(0,0)), Move.MoveType.PROMOTION);
        assertTrue(new PawnValidator(gs).isValidMove(mv));

        GameState nextGs = new PawnValidator(gs).simulateMove(mv);
        assertTrue(new PawnValidator(nextGs).isInCheck(nextGs, Color.BLACK));
    }
    
    @Test
    void whitePawnPromotion_InvalidMoveTypeNormal() {
        Board b = new BoardBuilder()
                .add(wp(1, 0)) 
                .add(wK(7, 4)).add(bK(2, 4))
                .build();
        GameState gs = gs(b, Color.WHITE);
       
        Move mv = new Move(p(1, 0), p(0, 0), Move.MoveType.NORMAL); 
        assertFalse(new PawnValidator(gs).isValidMove(mv));
    }

    @Test
    void whitePawnPromotion_NullPromotionPiece() {
        Board b = new BoardBuilder()
                .add(wp(1, 0))
                .add(wK(7, 4)).add(bK(2, 4))
                .build();
        GameState gs = gs(b, Color.WHITE);
        Move mv = new Move(p(1, 0), p(0, 0), null, Move.MoveType.PROMOTION);
        assertFalse(new PawnValidator(gs).isValidMove(mv));
    }
    
    @Test
    void whitePawnPromotion_InvalidPromotionPieceType_ToPawn() {
        Board b = new BoardBuilder()
                .add(wp(1, 0))
                .add(wK(7, 4)).add(bK(2, 4))
                .build();
        GameState gs = gs(b, Color.WHITE);

        Move mv = new Move(p(1, 0), p(0, 0), new Pawn(Color.WHITE, p(0,0)), Move.MoveType.PROMOTION);
        assertFalse(new PawnValidator(gs).isValidMove(mv));
    }

    @Test
    void whitePawnPromotion_LeavesKingInCheck() {
        Board b = new BoardBuilder()
                .add(wp(1, 0))
                .add(wK(1, 1))      
                .add(br(1, 7))      
                                   
                .add(bK(3,3))
                .build();
        GameState gs = gs(b, Color.WHITE);
        Move mv = new Move(p(1,0), p(0,0), new Queen(Color.WHITE, p(0,0)), Move.MoveType.PROMOTION); 
        assertFalse(new PawnValidator(gs).isValidMove(mv));
    }
}
