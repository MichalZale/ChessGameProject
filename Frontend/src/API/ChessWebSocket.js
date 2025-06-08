import { useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';

export function useChessWebSocket(gameId, onMessage) {
  const clientRef = useRef(null);

  useEffect(() => {
    if (!gameId) return;
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 2000,
      debug: str => console.log(str)
    });

    client.onConnect = () => {
      client.subscribe(`/topic/game-${gameId}`, message => {
        console.log("ODEBRANA wiadomość z WS:", message.body);
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
    }
  }

  return sendGameMessage;
}

