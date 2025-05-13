package com.example.ChessProject.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.ChessProject.model.Color;
import com.example.ChessProject.model.Move;
import com.example.ChessProject.model.Position;
import com.example.ChessProject.model.pieces.Pawn;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MoveSerializerTest {
    // Helper to create a simple move for testing
    private Move createTestMove(int r1, int c1, int r2, int c2, Move.MoveType type) {
        return new Move(new Position(r1, c1), new Position(r2, c2), type);
    }

    private Move createTestPromotionMove(int r1, int c1, int r2, int c2) {
        return new Move(new Position(r1, c1), new Position(r2, c2), new Pawn(Color.WHITE, new Position(r2,c2)), Move.MoveType.PROMOTION);
    }


    @Test
    void serializeAndDeserializeEmptyList() throws JsonProcessingException {
        List<Move> emptyList = new ArrayList<>();
        String json = MoveListSerializer.serializeMoveList(emptyList);
        assertNotNull(json);
        assertEquals("[]", json);

        List<Move> deserializedList = MoveListSerializer.deserializeMoveList(json);
        assertNotNull(deserializedList);
        assertTrue(deserializedList.isEmpty());
    }

    @Test
    void serializeAndDeserializeSingleMove() throws JsonProcessingException {
        List<Move> moves = new ArrayList<>();
        Move move1 = createTestMove(1, 1, 2, 2, Move.MoveType.NORMAL);
        moves.add(move1);

        String json = MoveListSerializer.serializeMoveList(moves);
        assertNotNull(json);
        // System.out.println("Single Move JSON: " + json); // For debugging

        List<Move> deserializedList = MoveListSerializer.deserializeMoveList(json);
        assertNotNull(deserializedList);
        assertEquals(1, deserializedList.size());
        assertEquals(move1, deserializedList.get(0)); // Assumes Move has a proper equals() method
    }

    @Test
    void serializeAndDeserializeMultipleMoves() throws JsonProcessingException {
        List<Move> moves = new ArrayList<>();
        Move move1 = createTestMove(6, 4, 4, 4, Move.MoveType.PAWN_DOUBLE_PUSH);
        Move move2 = createTestMove(1, 3, 3, 3, Move.MoveType.PAWN_DOUBLE_PUSH);
        moves.add(move1);
        moves.add(move2);

        String json = MoveListSerializer.serializeMoveList(moves);
        assertNotNull(json);
        // System.out.println("Multiple Moves JSON: " + json); // For debugging

        List<Move> deserializedList = MoveListSerializer.deserializeMoveList(json);
        assertNotNull(deserializedList);
        assertEquals(2, deserializedList.size());
        assertEquals(move1, deserializedList.get(0));
        assertEquals(move2, deserializedList.get(1));
    }

    @Test
    void serializeAndDeserializePromotionMove() throws JsonProcessingException {
        List<Move> moves = new ArrayList<>();
        // Ensure your Piece subclasses (like Pawn) and Piece itself are configured for Jackson polymorphism
        // with @JsonTypeInfo, @JsonSubTypes, and @JsonTypeName, and have no-arg constructors.
        Move promotionMove = createTestPromotionMove(1,0,0,0);
        moves.add(promotionMove);

        String json = MoveListSerializer.serializeMoveList(moves);
        assertNotNull(json);
        // System.out.println("Promotion Move JSON: " + json);

        List<Move> deserializedList = MoveListSerializer.deserializeMoveList(json);
        assertNotNull(deserializedList);
        assertEquals(1, deserializedList.size());
        Move deserializedMove = deserializedList.get(0);
        assertEquals(promotionMove, deserializedMove);
        assertNotNull(deserializedMove.getPromotionTo());
        assertTrue(deserializedMove.getPromotionTo() instanceof Pawn);
    }
}
