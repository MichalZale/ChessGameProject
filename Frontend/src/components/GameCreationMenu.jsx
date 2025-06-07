import React, { useState } from "react";

export default function GameCreationMenu({ onReturn, onCreate }) {
  const [whiteTime, setWhiteTime] = useState(300);
  const [blackTime, setBlackTime] = useState(300);
  const [whiteInc, setWhiteInc] = useState(5);
  const [blackInc, setBlackInc] = useState(5);
  const [playerColor, setPlayerColor] = useState("white"); 
  const [inviteCode, setInviteCode] = useState(null); 
  const [isLoading, setIsLoading] = useState(false); 

  const handleCreateGame = () => {
    setIsLoading(true);
    setInviteCode(null);
    
    onCreate({ whiteTime, blackTime, whiteInc, blackInc, playerColor }, (newInviteCode) => {
      setInviteCode(newInviteCode);
      setIsLoading(false);
    }, () => {
      setIsLoading(false);
      console.error("Failed to create game or get invite code.");
    });
  };

  return (
    <div className="game-creation-menu">
      <h2>Create New Game</h2>

      <div className="form-row color-selection">
        <label>Your Color:</label>
        <div className="radio-group">
          <label className={playerColor === "white" ? "active" : ""}>
            <input
              type="radio"
              name="playerColor"
              value="white"
              checked={playerColor === "white"}
              onChange={() => setPlayerColor("white")}
            />
            White
          </label>
          <label className={playerColor === "black" ? "active" : ""}>
            <input
              type="radio"
              name="playerColor"
              value="black"
              checked={playerColor === "black"}
              onChange={() => setPlayerColor("black")}
            />
            Black
          </label>
        </div>
      </div>

      <div className="form-row">
        <label htmlFor="white-time">White's time (seconds):</label>
        <input
          id="white-time"
          type="range"
          min="10"
          max="3600"
          step="10"
          value={whiteTime}
          onChange={e => setWhiteTime(Number(e.target.value))}
        />
        <span className="slider-value">{whiteTime}s</span>
      </div>
      <div className="form-row">
        <label htmlFor="black-time">Black's time (seconds):</label>
        <input
          id="black-time"
          type="range"
          min="10"
          max="3600"
          step="10"
          value={blackTime}
          onChange={e => setBlackTime(Number(e.target.value))}
        />
        <span className="slider-value">{blackTime}s</span>
      </div>
      <div className="form-row">
        <label htmlFor="white-inc">White's increment (seconds):</label>
        <input
          id="white-inc"
          type="range"
          min="0"
          max="60"
          step="1"
          value={whiteInc}
          onChange={e => setWhiteInc(Number(e.target.value))}
        />
        <span className="slider-value">{whiteInc}s</span>
      </div>
      <div className="form-row">
        <label htmlFor="black-inc">Black's increment (seconds):</label>
        <input
          id="black-inc"
          type="range"
          min="0"
          max="60"
          step="1"
          value={blackInc}
          onChange={e => setBlackInc(Number(e.target.value))}
        />
        <span className="slider-value">{blackInc}s</span>
      </div>

      {isLoading && <p className="loading-text">Creating game...</p>}
      {inviteCode && !isLoading && (
        <div className="invite-code-section">
          <p>Game created! Share this code to invite a friend:</p>
          <strong className="invite-code-display">{inviteCode}</strong>
          <p className="info-text">(You will be redirected to the game shortly)</p>
        </div>
      )}

      <div className="button-row">
        <button onClick={onReturn} disabled={isLoading}>Return</button>
        <button onClick={handleCreateGame} disabled={isLoading || inviteCode}>
          {inviteCode ? "Game Created" : "Create Game"}
        </button>
      </div>
    </div>
  );
}