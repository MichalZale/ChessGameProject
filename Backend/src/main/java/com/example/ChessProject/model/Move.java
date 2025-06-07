package com.example.ChessProject.model;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    public Move(@JsonProperty("from") Position from,
            @JsonProperty("to") Position to,
            @JsonProperty("promotionTo") Piece promotionTo,
            @JsonProperty("moveType") MoveType moveType){
        this.from=from;
        this.to=to;
        this.promotionTo=promotionTo;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Objects.equals(from, move.from) &&
               Objects.equals(to, move.to) &&
               Objects.equals(promotionTo, move.promotionTo) &&
               moveType == move.moveType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, promotionTo, moveType);
    }
}
