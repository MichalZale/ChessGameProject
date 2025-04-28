package com.example.ChessProject.model;
public class Position {
    public int row;
    public int column;
    public Position(int row, int column){
        this.row=row;
        this.column=column;
    }

    public Position offset(int dr, int dc){
        return new Position(this.row + dr, this.column + dc);
    }

    public Position clone(){
        return new Position(row, column);
    }
    public boolean equals(Position pos){
        return this.row==pos.row && this.column==pos.column;
    }
}
