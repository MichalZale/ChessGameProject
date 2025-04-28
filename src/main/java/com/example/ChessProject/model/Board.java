package com.example.ChessProject.model;

public class Board {
    private Piece[][] board = new Piece[8][8];
    
    public Piece getPiece(Position pos){
        return board[pos.row][pos.column];
    }

    public void setPiece(Position pos, Piece piece){
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
                Piece piece = this.getPiece(new Position(r, c));
                clone.board[r][c]=piece;
            }
        }
        return clone;
    }
}
