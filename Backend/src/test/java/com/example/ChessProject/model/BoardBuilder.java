package com.example.ChessProject.model;
import com.example.ChessProject.model.pieces.*;

public class BoardBuilder {
    private final Board board = new Board();

    public BoardBuilder add(Piece p){
        board.setPiece(p.getPosition(), p);
        return this;
    }
    public Board build(){ return board; }

    public static Position  p(int r,int c){ return new Position(r,c); }
    public static Pawn      wp(int r,int c){ return new Pawn (Color.WHITE,p(r,c)); }
    public static Pawn      bp(int r,int c){ return new Pawn (Color.BLACK,p(r,c)); }
    public static Knight    wk(int r,int c){ return new Knight(Color.WHITE,p(r,c)); }
    public static Knight    bk(int r,int c){ return new Knight(Color.BLACK,p(r,c)); }
    public static Bishop    wb(int r,int c){ return new Bishop(Color.WHITE,p(r,c)); }
    public static Bishop    bb(int r,int c){ return new Bishop(Color.BLACK,p(r,c)); }
    public static Rook      wr(int r,int c){ return new Rook  (Color.WHITE,p(r,c)); }
    public static Rook      br(int r,int c){ return new Rook  (Color.BLACK,p(r,c)); }
    public static Queen     wq(int r,int c){ return new Queen (Color.WHITE,p(r,c)); }
    public static Queen     bq(int r,int c){ return new Queen (Color.BLACK,p(r,c)); }
    public static King      wK(int r,int c){ return new King  (Color.WHITE,p(r,c)); }
    public static King      bK(int r,int c){ return new King  (Color.BLACK,p(r,c)); }

    public static GameState gs(Board b, Color side){ return new GameState(b,side,true,true,true,true,null); }
}
