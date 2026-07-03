import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';

export function useChessWebSocket(gameId, onMessage) {
  const clientRef = useRef(null);

  useEffect(() => {
    if (!gameId) return;
    const wsUrl = import.meta.env.VITE_WS_URL || 'ws://localhost:8080/ws';
    const client = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 2000,
      debug: () => {}
    });

    client.onConnect = () => {
      client.subscribe(`/topic/game-${gameId}`, message => {
        const body = JSON.parse(message.body);
        onMessage && onMessage(body);
      });
    };

    client.activate();
    clientRef.current = client;

    return () => {
      if (clientRef.current) clientRef.current.deactivate();
    };
  }, [gameId, onMessage]);

    function sendGameMessage(wsMessage) {
    if (clientRef.current && clientRef.current.connected) {
      clientRef.current.publish({
        destination: '/app/game/message',
        body: JSON.stringify(wsMessage)
      });
      return true;
    }
    console.error("WebSocket is not connected. Message was not sent:", wsMessage);
    return false;
  }

  return sendGameMessage;
}
