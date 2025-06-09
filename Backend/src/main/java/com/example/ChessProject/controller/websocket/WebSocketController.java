package com.example.ChessProject.controller.websocket;

import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.controller.dto.GameResponseMapper;
import com.example.ChessProject.controller.websocket.websocketMessage.WsMessage;
import com.example.ChessProject.controller.websocket.websocketMessage.WsType;
import com.example.ChessProject.model.Color;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.Move;
import com.example.ChessProject.model.Piece;
import com.example.ChessProject.model.Position;
import com.example.ChessProject.model.pieces.Bishop;
import com.example.ChessProject.model.pieces.Knight;
import com.example.ChessProject.model.pieces.Pawn;
import com.example.ChessProject.model.pieces.Queen;
import com.example.ChessProject.model.pieces.Rook;
import com.example.ChessProject.service.GameService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/message")
    public void handleGameMessage(WsMessage msg) {
        int gameId = msg.getGameId();
        WsType type = msg.getType();
        int userId = msg.getUserId();

        try {
            Game updatedGame;
            switch (type) {
                case MOVE:
                    System.out.print("Otrzymano wiadomość MOVE!");
                    Move move = parseMove(msg.getPayload(), gameId);
                    System.out.print("Ruch" + move);
                    updatedGame = gameService.makeMove(gameId, userId, move);
                    System.out.println("Ruszono się.");
                    System.out.println("Stan planszy przed wysłaniem odpowiedzi:");
                    System.out.println(updatedGame.getGameState().getBoard().toString());
                    break;

                case RESIGN:
                    updatedGame = gameService.resign(gameId, userId);
                    break;

                case DRAW_PROPOSE:
                    updatedGame = gameService.proposeDraw(gameId, userId);
                    if (updatedGame != null) {
                        System.out.println("WebSocketController: updatedGame after proposeDraw - isDrawOffered: "
                                + updatedGame.isDrawOffered() + ", drawOfferedByUserID: "
                                + updatedGame.getDrawOfferedByUserID());
                    } else {
                        System.out.println("WebSocketController: updatedGame is NULL after proposeDraw!");
                    }
                    break;

                case DRAW_ACCEPT:
                    updatedGame = gameService.acceptDraw(gameId, userId);
                    break;

                case DRAW_REJECT:
                    updatedGame = gameService.rejectDraw(gameId, userId);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported WebSocket message type: " + type);
            }
            GameResponse response = GameResponseMapper.toGameResponse(updatedGame);
            System.out.println("<<<<< WebSocket Sending (WebSocketController) >>>>>");
            if (response != null) {
                System.out.println("Response to be sent: gameId=" + response.getGameId() +
                        ", isDrawOffered=" + response.isDrawOffered() + // KLUCZOWY LOG
                        ", drawOfferedByUserID=" + response.getDrawOfferedByUserID());
                // Możesz też zalogować cały obiekt, jeśli masz skonfigurowanego Jacksona do
                // ładnego drukowania lub użyć jego metody toString()
                // com.fasterxml.jackson.databind.ObjectMapper objectMapper = new
                // com.fasterxml.jackson.databind.ObjectMapper();
                // System.out.println("Full response JSON: " +
                // objectMapper.writeValueAsString(response));
            } else {
                System.out.println("Response to be sent is NULL!");
            }

            messagingTemplate.convertAndSend("/topic/game-" + gameId, response);
            System.out.println("Wysłano zaktualizowany stan gry dla typu: " + type);

        } catch (Exception e) {
            System.err.println(
                    "Error processing WebSocket message for game " + gameId + ", type " + type + ": " + e.getMessage());
            e.printStackTrace();

            messagingTemplate.convertAndSend("/topic/game-" + gameId, Map.of("error", e.getMessage()));
        }
    }

    private Move parseMove(Object payload, int gameId) {
        if (payload instanceof Map) {
            @SuppressWarnings("unchecked") // Bezpieczne, bo sprawdzamy instanceof
            Map<String, Object> map = (Map<String, Object>) payload;

            @SuppressWarnings("unchecked")
            Map<String, Object> fromMap = (Map<String, Object>) map.get("from");
            @SuppressWarnings("unchecked")
            Map<String, Object> toMap = (Map<String, Object>) map.get("to");

            if (fromMap == null || toMap == null) {
                throw new IllegalArgumentException("Invalid payload: 'from' or 'to' is missing.");
            }

            int fromRow = (int) fromMap.get("row");
            int fromCol = (int) fromMap.get("column");
            Position fromPos = new Position(fromRow, fromCol);

            int toRow = (int) toMap.get("row");
            int toCol = (int) toMap.get("column");
            Position toPos = new Position(toRow, toCol);

            String moveTypeStr = (String) map.get("moveType");
            if (moveTypeStr == null) {
                throw new IllegalArgumentException("Invalid payload: 'moveType' is missing.");
            }
            Move.MoveType moveType = Move.MoveType.valueOf(moveTypeStr.toUpperCase());

            Piece promotionToPiece = null;
            // Frontend wysyła string, np. "QUEEN"
            Object promoObj = map.get("promotionTo");

            if (moveType == Move.MoveType.PROMOTION && promoObj instanceof String) {
                String promotionToString = (String) promoObj;
                System.out.println("Promotion detected. promotionToString: " + promotionToString);

                Game game = gameService.getGameState(gameId);
                if (game == null) {
                    System.err.println("Game not found with id: " + gameId + " during promotion parsing.");
                    throw new IllegalStateException("Game not found for promotion: " + gameId);
                }

                Piece pieceBeingPromoted = game.getGameState().getBoard().getPiece(fromPos);
                if (pieceBeingPromoted == null || !(pieceBeingPromoted instanceof Pawn)) {
                    System.err.println("No pawn found at promotion source square " + fromPos + " for game " + gameId);
                    throw new IllegalStateException("Pawn not found at promotion source square: " + fromPos);
                }

                Color pawnColor = pieceBeingPromoted.getColor();
                System.out.println("Pawn color for promotion: " + pawnColor);

                switch (promotionToString.toUpperCase()) {
                    case "QUEEN":
                        promotionToPiece = new Queen(pawnColor, toPos);
                        break;
                    case "ROOK":
                        promotionToPiece = new Rook(pawnColor, toPos);
                        break;
                    case "BISHOP":
                        promotionToPiece = new Bishop(pawnColor, toPos);
                        break;
                    case "KNIGHT":
                        promotionToPiece = new Knight(pawnColor, toPos);
                        break;
                    default:
                        System.err.println(
                                "Invalid promotion piece string: " + promotionToString + " for game " + gameId);
                        throw new IllegalArgumentException("Invalid piece type for promotion: " + promotionToString);
                }
                System.out.println("Created promotion piece: " + promotionToPiece);

            } else if (moveType == Move.MoveType.PROMOTION && promoObj == null) {
                System.err
                        .println("Promotion move type specified but no promotionTo piece provided for game " + gameId);
                throw new IllegalArgumentException("Promotion move type specified but no promotionTo piece provided.");
            } else if (moveType == Move.MoveType.PROMOTION && !(promoObj instanceof String)) {
                System.err.println("Promotion move type specified but promotionTo is not a String: " + promoObj
                        + " for game " + gameId);
                throw new IllegalArgumentException("Invalid format for promotionTo field.");
            }

            return new Move(fromPos, toPos, promotionToPiece, moveType);
        }
        System.out.println("Invalid payload for move: payload is not an instance of Map. Payload:" + payload);
        throw new IllegalArgumentException("Invalid payload for move: not a Map");
    }
}
