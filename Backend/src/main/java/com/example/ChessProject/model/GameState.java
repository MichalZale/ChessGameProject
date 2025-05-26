package com.example.ChessProject.model;

public class GameState {
    private Board board;
    private Color sideToMove;
    private boolean wCastleK, wCastleQ, bCastleK, bCastleQ;
    private Position enPassant;
    public GameState(Board b, Color side,
                     boolean wCK, boolean wCQ,
                     boolean bCK, boolean bCQ,
                     Position ep) {
        this.board = b;
        this.sideToMove = side;
        this.wCastleK = wCK; this.wCastleQ = wCQ;
        this.bCastleK = bCK; this.bCastleQ = bCQ;
        this.enPassant = ep;
    }

    public Board getBoard()               { return board; }
    public Color getSideToMove()          { return sideToMove; }
    public boolean canWhiteCastleK()      { return wCastleK; }
    public boolean canWhiteCastleQ()      { return wCastleQ; }
    public boolean canBlackCastleK()      { return bCastleK; }
    public boolean canBlackCastleQ()      { return bCastleQ; }
    public Position getEnPassant()        { return enPassant; }

    public void setBoard(Board board) { this.board = board; }
    public void setSideToMove(Color sideToMove) { this.sideToMove = sideToMove; }
    public void setWCastleK(boolean wCastleK) { this.wCastleK = wCastleK; }
    public void setWCastleQ(boolean wCastleQ) { this.wCastleQ = wCastleQ; }
    public void setBCastleK(boolean bCastleK) { this.bCastleK = bCastleK; }
    public void setBCastleQ(boolean bCastleQ) { this.bCastleQ = bCastleQ; }
    public void setEnPassant(Position enPassant) { this.enPassant = enPassant; }

    public void switchSideToMove(){
        if(sideToMove==Color.WHITE){
            sideToMove=Color.BLACK;
            return;
        }
        sideToMove=Color.WHITE;
        return;
    }
    
    public GameState clone() {
        return new GameState(board.clone(), sideToMove, 
        wCastleK, wCastleQ, 
        bCastleK, bCastleQ, 
        (enPassant==null) ? null : enPassant.clone());
    }
    
}
