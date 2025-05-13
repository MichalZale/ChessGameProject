package com.example.ChessProject.model.pieces;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.ChessProject.model.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("king")

public class King extends Piece {
    private boolean hasMoved;

    public King(
            @JsonProperty("color") Color c, 
            @JsonProperty("column") Position p) {
        super(c, p);
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    @Override
    public Piece clone() {
        King king = new King(this.getColor(), this.getPosition());
        king.hasMoved = this.hasMoved;
        return king;
    }

    @Override
    public List<Position> getPseudoMoves(GameState state) {
        List<Position> moves = new ArrayList<>();
        Board board = state.getBoard();
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0)
                    continue;
                Position to = position.offset(dr, dc);
                if (board.isInside(to)) {
                    Piece t = board.getPiece(to);
                    if (t == null || t.getColor() != color)
                        moves.add(to);
                }
            }
        boolean white = color == Color.WHITE;
        if (white ? state.canWhiteCastleK() : state.canBlackCastleK()) {
            if (board.isEmpty(position.offset(0, 1)) && board.isEmpty(position.offset(0, 2)))
                moves.add(position.offset(0, 2));
        }
        if (white ? state.canWhiteCastleQ() : state.canBlackCastleQ()) {
            if (board.isEmpty(position.offset(0, -1)) && board.isEmpty(position.offset(0, -2))
                    && board.isEmpty(position.offset(0, -3)))
                moves.add(position.offset(0, -2));
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
        King king = (King) o;
        return hasMoved == king.hasMoved();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hasMoved);
    }

}
