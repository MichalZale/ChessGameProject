package com.example.ChessProject.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import com.example.ChessProject.model.pieces.King;
import com.example.ChessProject.model.pieces.Queen;
import com.example.ChessProject.model.pieces.Rook;

public class BoardTest {
    @Test
    void startingBoardPlacesKingsAndQueensOnStandardSquares() {
        Board board = BoardFactory.createBoard();

        assertInstanceOf(Queen.class, board.getPiece(new Position(0, 3)));
        assertInstanceOf(King.class, board.getPiece(new Position(0, 4)));
        assertInstanceOf(Queen.class, board.getPiece(new Position(7, 3)));
        assertInstanceOf(King.class, board.getPiece(new Position(7, 4)));

        assertEquals(new Position(0, 3), board.getPiece(new Position(0, 3)).getPosition());
        assertEquals(new Position(0, 4), board.getPiece(new Position(0, 4)).getPosition());
        assertEquals(new Position(7, 3), board.getPiece(new Position(7, 3)).getPosition());
        assertEquals(new Position(7, 4), board.getPiece(new Position(7, 4)).getPosition());
    }

    @Test
    void setPieceKeepsPiecePositionInSyncWithBoardSquare() {
        Board board = new Board();
        Position target = new Position(3, 5);
        Rook rook = new Rook(Color.WHITE, new Position(0, 0));

        board.setPiece(target, rook);

        assertEquals(target, rook.getPosition());
        assertEquals(target, board.getPiece(target).getPosition());
    }
}
