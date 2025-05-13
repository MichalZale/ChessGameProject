package com.example.ChessProject.model;

public class Move {
    public enum MoveType {
        NORMAL,
        EN_PASSANT,
        PAWN_DOUBLE_PUSH,
        CASTLE_KINGSIDE,
        CASTLE_QUEENSIDE,
        PROMOTION
    }
    
    private Position from;
    private Position to;
    private Piece promotionTo;
    private MoveType moveType;
    public Move(Position from, Position to, Piece promotion, MoveType moveType){
        this.from=from;
        this.to=to;
        this.promotionTo=promotion;
        this.moveType=moveType;
    }

    public Move(Position from, Position to, MoveType type) {
        this(from, to, null, type);
    }

    public Move(Position from, Position to){
        this(from, to, null, MoveType.NORMAL);
    }

    public Position getFrom(){return from;}
    public Position getTo(){return to;}
    public Piece getPromotionTo(){return this.promotionTo;}
    public MoveType getMoveType(){return this.moveType;}
}
