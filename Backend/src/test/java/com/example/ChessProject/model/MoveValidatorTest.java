package com.example.ChessProject.model;

import static com.example.ChessProject.model.BoardBuilder.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.example.ChessProject.model.validators.QueenValidator;

public class MoveValidatorTest {

    @Test
    void cannotCaptureOpponentKing() {
        Board board = new BoardBuilder()
                .add(wK(7, 4))
                .add(wq(4, 4))
                .add(bK(4, 7))
                .build();
        GameState state = gs(board, Color.WHITE);

        Move move = new Move(p(4, 4), p(4, 7));

        assertFalse(new QueenValidator(state).isValidMove(move));
    }
}
