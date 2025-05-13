package com.example.ChessProject.model.pieces;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.ChessProject.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("rook")

public class Rook extends Piece {
    private boolean hasMoved;

    public boolean hasMoved() {
        return hasMoved;
    }

    public Rook(
            @JsonProperty("color") Color c, 
            @JsonProperty("column") Position p) {
        super(c, p);
    }

    @Override
    public Piece clone() {
        Rook rook = new Rook(getColor(), getPosition());
        rook.hasMoved = this.hasMoved;
        return rook;
    }

    @Override
    public List<Position> getPseudoMoves(GameState state) {
        List<Position> moves = new ArrayList<>();
        Board board = state.getBoard();
        int[][] dir = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        for (int[] v : dir) {
            Position cur = position.offset(v[0], v[1]);
            while (board.isInside(cur)) {
                if (board.isEmpty(cur))
                    moves.add(cur);
                else {
                    if (board.getPiece(cur).getColor() != color)
                        moves.add(cur);
                    break;
                }
                cur = cur.offset(v[0], v[1]);
            }
        }
        return moves;
    }

    public void setHasMoved(boolean b){
        this.hasMoved = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Rook rook = (Rook) o;
        return hasMoved == rook.hasMoved();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hasMoved);
    }

}
