package com.example.ChessProject.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Board {
    private Piece[][] board = new Piece[8][8];

    public Piece getPiece(Position pos){
        return board[pos.row][pos.column];
    }

    public void setPiece(Position pos, Piece piece){
        if (piece != null) {
            piece.setPosition(pos);
        }
        board[pos.row][pos.column] = piece;
    }

    public boolean isEmpty(Position pos){
        return getPiece(pos) == null;
    }
    public boolean isInside(Position pos){
        return pos.row>=0 && pos.row<8 && pos.column>=0 && pos.column<8;
    }

    public Board clone(){
        Board clone = new Board();
        for(int r=0;r<8;r++){
            for(int c=0;c<8;c++){
                Piece piece = this.board[r][c];
                if (piece != null) {
                    clone.board[r][c] = piece.clone();
                    clone.board[r][c].setPosition(new Position(r, c));
                } else {
                    clone.board[r][c] = null; 
                }
            }
        }
        return clone;
    }

    public Piece[][] asArray(){
        return this.board;
    }
}
