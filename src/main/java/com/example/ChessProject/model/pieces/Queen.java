package com.example.ChessProject.model.pieces;

import java.util.ArrayList;
import java.util.List;

import com.example.ChessProject.model.*;

public class Queen extends Piece {
    public Queen(Color c, Position p) {
        super(c, p);
    }

    @Override
    public Piece clone() {
        return new Queen(getColor(), getPosition());
    }

    @Override
    public List<Position> getPseudoMoves(GameState state) {
        List<Position> moves = new ArrayList<>();
        Board board = state.getBoard();
        int[][] dir = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
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

}
