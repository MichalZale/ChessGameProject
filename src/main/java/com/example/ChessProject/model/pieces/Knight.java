package com.example.ChessProject.model.pieces;

import java.util.ArrayList;
import java.util.List;

import com.example.ChessProject.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("knight")

public class Knight extends Piece {
    public Knight(
            @JsonProperty("color") Color c, 
            @JsonProperty("column") Position p) {
        super(c, p);
    }

    @Override
    public Piece clone() {
        return new Knight(getColor(), getPosition());
    }

    @Override
    public List<Position> getPseudoMoves(GameState state) {
        List<Position> moves = new ArrayList<>();
        Board board = state.getBoard();
        int[][] d = { { 2, 1 }, { 1, 2 }, { -1, 2 }, { -2, 1 }, { -2, -1 }, { -1, -2 }, { 1, -2 }, { 2, -1 } };
        for (int[] v : d) {
            Position to = position.offset(v[0], v[1]);
            if (board.isInside(to)) {
                Piece t = board.getPiece(to);
                if (t == null || t.getColor() != color)
                    moves.add(to);
            }
        }
        return moves;
    }

    

}
