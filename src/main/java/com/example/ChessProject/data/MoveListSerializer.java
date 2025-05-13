package com.example.ChessProject.data;

import java.util.List;

import com.example.ChessProject.model.Move;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MoveListSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Serializes a list of Move objects to a JSON string.
     * @param moveList The list of moves.
     * @return JSON string representation of the move list.
     * @throws JsonProcessingException if an error occurs during serialization.
     */
    public static String serializeMoveList(List<Move> moveList) throws JsonProcessingException {
        return objectMapper.writeValueAsString(moveList);
    }

    /**
     * Deserializes a JSON string back into a List of Move objects.
     * @param jsonMoveList The JSON string representing the list of moves.
     * @return A List of Move objects.
     * @throws JsonProcessingException if an error occurs during deserialization.
     */
    public static List<Move> deserializeMoveList(String jsonMoveList) throws JsonProcessingException {
        return objectMapper.readValue(jsonMoveList, new TypeReference<List<Move>>() {});
    }
}
