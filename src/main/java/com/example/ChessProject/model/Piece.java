package com.example.ChessProject.model;
import java.util.List;
public abstract class Piece {
    protected Color color;
    protected Position position;

    public Piece(Color c, Position p){
        this.color = c;
        this.position = p;
    }

    public void setPosition(Position pos){
        this.position=pos;
    }

    public abstract List<Position> getPseudoMoves(GameState gameState);
    public Color getColor(){return color;}
    public Position getPosition(){return position;}
    public abstract Piece clone();
}
