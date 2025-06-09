package com.example.ChessProject.model;

import java.util.List;
import java.util.Objects;

import com.example.ChessProject.model.pieces.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pawn.class, name = "pawn"),
        @JsonSubTypes.Type(value = Rook.class, name = "rook"),
        @JsonSubTypes.Type(value = Knight.class, name = "knight"),
        @JsonSubTypes.Type(value = Bishop.class, name = "bishop"),
        @JsonSubTypes.Type(value = Queen.class, name = "queen"),
        @JsonSubTypes.Type(value = King.class, name = "king")
})

public abstract class Piece {
    protected Color color;
    protected Position position;

    public Piece(Color c, Position p) {
        this.color = c;
        this.position = p;
    }

    public Piece() {
    }

    public void setPosition(Position pos) {
        this.position = pos;
    }

    public abstract List<Position> getPseudoMoves(GameState gameState);

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public abstract Piece clone();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Piece piece = (Piece) o;
        return color == piece.color && Objects.equals(position, piece.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, position, getClass());
    }

    public static Piece of(String type, String color, Position position) {
        Color colorEnum = color.equalsIgnoreCase("WHITE") ? Color.WHITE : Color.BLACK;
        switch (type.toUpperCase()) {
            case "QUEEN":
                return new Queen(colorEnum, position);
            case "ROOK":
                return new Rook(colorEnum, position);
            case "BISHOP":
                return new Bishop(colorEnum, position);
            case "KNIGHT":
                return new Knight(colorEnum, position);
            case "PAWN":
                return new Pawn(colorEnum, position);
            case "KING":
                return new King(colorEnum, position);
            default:
                throw new IllegalArgumentException("Unknown piece: " + type);
        }
    }
}
