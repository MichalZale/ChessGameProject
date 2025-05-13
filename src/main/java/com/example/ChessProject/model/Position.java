package com.example.ChessProject.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {
    public int row;
    public int column;
    public Position(@JsonProperty("row") int row,
                    @JsonProperty("column") int column){
        this.row=row;
        this.column=column;
    }

    public Position offset(int dr, int dc){
        return new Position(this.row + dr, this.column + dc);
    }

    public Position clone(){
        return new Position(row, column);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && column == position.column;
    }

    // It's good practice to override hashCode when you override equals
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
