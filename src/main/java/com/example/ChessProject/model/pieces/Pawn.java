package com.example.ChessProject.model.pieces;
import java.util.ArrayList;
import java.util.List;

import com.example.ChessProject.model.*;

public class Pawn extends Piece{
    private boolean hasMoved;
    private boolean justDoubleMoved;
    public boolean hasMoved(){return hasMoved;}
    public boolean justDoubleMoved(){return justDoubleMoved;}

    public Pawn(Color c, Position p){
        super(c, p);
    }

    @Override
    public Piece clone(){
        Pawn pawn = new Pawn(getColor(), getPosition());
        pawn.hasMoved = this.hasMoved;
        pawn.justDoubleMoved = this.justDoubleMoved;
        return pawn;
    }

    @Override
    public List<Position> getPseudoMoves(GameState state){
        List<Position> moves = new ArrayList<>();
        Board board = state.getBoard();
        int dir  = color==Color.WHITE ? -1 : 1;
        int start = color==Color.WHITE ? 6 : 1;
    
        Position one = position.offset(dir,0);
        if(board.isInside(one) && board.isEmpty(one)){
            moves.add(one);
            Position two = position.offset(2*dir,0);
            if(position.row==start && board.isEmpty(two)) moves.add(two);
        }
        Position dl = position.offset(dir,-1);
        if(board.isInside(dl)){
            Piece t = board.getPiece(dl);
            if(t!=null && t.getColor()!=color) moves.add(dl);
            if(state.getEnPassant()!=null && state.getEnPassant().equals(dl)) moves.add(dl);
        }
        Position dr = position.offset(dir,1);
        if(board.isInside(dr)){
            Piece t = board.getPiece(dr);
            if(t!=null && t.getColor()!=color) moves.add(dr);
            if(state.getEnPassant()!=null && state.getEnPassant().equals(dr)) moves.add(dr);
        }
        return moves;
    }

}
