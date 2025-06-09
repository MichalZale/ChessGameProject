package com.example.ChessProject.model.validators;
import com.example.ChessProject.model.*;
import com.example.ChessProject.model.MoveValidator;

public class KingValidator extends MoveValidator {
    public KingValidator(GameState s){ super(s); }

    @Override
    public boolean basicMove(Move move, GameState st){
        Position from=move.getFrom(), to=move.getTo();
        Board b=st.getBoard();

        if(!b.isInside(from)||!b.isInside(to)) return false;
        if(b.getPiece(from)==null) return false;
        if(st.getSideToMove()!=b.getPiece(from).getColor()) return false;
        if(b.getPiece(to)!=null && b.getPiece(to).getColor()==st.getSideToMove()) return false;

        int dr=Math.abs(to.row-from.row);
        int dc=Math.abs(to.column-from.column);
        if (move.getMoveType() == Move.MoveType.CASTLE_KINGSIDE || move.getMoveType() == Move.MoveType.CASTLE_QUEENSIDE) {
            if (dr != 0 || dc != 2) return false; 

            if(!isCastlingLineSafe(from,to,st)) return false;
            if(from.column<to.column){ // Kingside
                if(st.getSideToMove()==Color.WHITE && !st.canWhiteCastleK()) return false;
                if(st.getSideToMove()==Color.BLACK && !st.canBlackCastleK()) return false;
            }else{ // Queenside
                if(st.getSideToMove()==Color.WHITE && !st.canWhiteCastleQ()) return false;
                if(st.getSideToMove()==Color.BLACK && !st.canBlackCastleQ()) return false;
            }
            if(!pathEmptyForCastle(from,to,b)) return false;
            return true;
        } else if (dr <= 1 && dc <= 1) { 
            return true;
        }
        return false;
    }

    @Override
    public boolean canAttack(Position from, Position target, Board b){
        int dr=Math.abs(target.row-from.row);
        int dc=Math.abs(target.column-from.column);
        return dr<=1 && dc<=1;
    }

    private boolean pathEmptyForCastle(Position from, Position to, Board b){
        int step = (to.column>from.column)?1:-1;
        int col  = from.column + step;
        while(col!=to.column){
            if(!b.isEmpty(new Position(from.row,col))) return false;
            col+=step;
        }
        return true;
    }
}
