package com.example.ChessProject.model.pieces;

import java.util.ArrayList;
import java.util.List;

import com.example.ChessProject.model.*;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonTypeName("bishop")

public class Bishop extends Piece {
    public Bishop(
            @JsonProperty("color") Color c, 
            @JsonProperty("position") Position p) {
        super(c, p);
    }

    @Override
    public Piece clone() {
        return new Bishop(this.getColor(), this.getPosition());
    }

    @Override
    public List<Position> getPseudoMoves(GameState state) {
        List<Position> moves = new ArrayList<>();
        Board board = state.getBoard();
        int[] step = { -1, 1 };
        for (int dr : step)
            for (int dc : step) {
                Position cur = position.offset(dr, dc);
                while (board.isInside(cur)) {
                    if (board.isEmpty(cur))
                        moves.add(cur);
                    else {
                        if (board.getPiece(cur).getColor() != color)
                            moves.add(cur);
                        break;
                    }
                    cur = cur.offset(dr, dc);
                }
            }
        return moves;
    }

}
