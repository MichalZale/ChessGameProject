package com.example.ChessProject.controller.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameResponseTest {

    @Test
    void serializesDrawOfferFlagWithFrontendPropertyName() throws Exception {
        GameResponse response = GameResponse.builder()
                .isDrawOffered(true)
                .drawOfferedByUserID(7)
                .build();

        JsonNode json = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(response));

        assertTrue(json.get("isDrawOffered").asBoolean());
        assertFalse(json.has("drawOffered"));
        assertEquals(7, json.get("drawOfferedByUserID").asInt());
    }
}
