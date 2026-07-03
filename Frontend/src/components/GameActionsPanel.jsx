import React from 'react';
import './GameActionsPanel.css';

export default function GameActionsPanel({
  gameData,
  currentUser,
  onOfferDraw,
  onResign,
  onAcceptDraw,
  onRejectDraw,
}) {
  if (!gameData || !currentUser) {
    return <div className="game-actions-panel"><p>Loading game actions...</p></div>;
  }

  const currentUserId = currentUser?.userID ?? currentUser?.id;
  const isPlayerInGame = currentUserId === gameData.whiteUserId || currentUserId === gameData.blackUserId;
  const gameIsOver = gameData.status === "FINISHED" || gameData.status === "ABANDONED" || gameData.gameResult !== "NONE";
  const drawOfferIsActive = gameData.isDrawOffered;
  const canRespondToDrawOffer = drawOfferIsActive && gameData.drawOfferedByUserID !== -1 && gameData.drawOfferedByUserID !== currentUserId && isPlayerInGame && !gameIsOver;
  const canOfferDraw = isPlayerInGame && !gameIsOver && !drawOfferIsActive;


  let gameStatusDisplay = null;
  if (gameIsOver) {
    switch (gameData.gameResult) {
      case "CHECKMATE":
        gameStatusDisplay = <p className="game-result">Checkmate! {gameData.gameState?.sideToMove === 'WHITE' ? 'Black' : 'White'} wins.</p>;
        break;
      case "STELMATE":
        gameStatusDisplay = <p className="game-result">Stalemate! It's a draw.</p>;
        break;
      case "RESIGNATION":
        gameStatusDisplay = <p className="game-result">Resignation. Game Over.</p>;
        break;
      case "DRAW_AGREEMENT":
        gameStatusDisplay = <p className="game-result">Draw by agreement.</p>;
        break;
      default:
        gameStatusDisplay = <p className="game-result">Game Over: {gameData.gameResult || gameData.status}</p>;
    }
  }


  return (
    <div className="game-actions-panel">
      <div className="player-names">
        <div className={`player-name ${gameData.gameState?.sideToMove === 'WHITE' ? 'active-turn' : ''}`}>
          White: Player {gameData.whiteUserId || 'N/A'}
        </div>
        <div className={`player-name ${gameData.gameState?.sideToMove === 'BLACK' ? 'active-turn' : ''}`}>
          Black: Player {gameData.blackUserId || 'N/A'}
        </div>
      </div>

      {gameStatusDisplay && <div className="game-status-display">{gameStatusDisplay}</div>}

      {isPlayerInGame && !gameIsOver && (
        <div className="action-buttons">
          {canOfferDraw && (
            <button onClick={onOfferDraw} className="action-button draw-button">
              Offer Draw
            </button>
          )}

          {canRespondToDrawOffer && (
            <div className="draw-response-section">
              <p className="draw-offer-text">Opponent offered a draw. Respond?</p>
              <button onClick={onAcceptDraw} className="action-button accept-draw-button">
                Accept Draw
              </button>
              <button onClick={onRejectDraw} className="action-button reject-draw-button">
                Reject Draw
              </button>
            </div>
          )}
          
          <button onClick={onResign} className="action-button resign-button">
            Resign
          </button>
        </div>
      )}
      {!isPlayerInGame && !gameIsOver && (
        <p>You are observing this game.</p>
      )}
    </div>
  );
}