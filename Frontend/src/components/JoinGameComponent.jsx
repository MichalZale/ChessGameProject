import React, { useState } from 'react';
import './JoinGameForm.css';

export default function JoinGameForm({ onJoin, onCancel }) {
  const [gameCode, setGameCode] = useState('');
  const [error, setError] = useState(null);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!gameCode.trim()) {
      setError('Please enter a game code');
      return;
    }
    
    onJoin(gameCode);
  };

  return (
    <div className="join-game-form">
      <h2>Join a Game</h2>
      <p className="info-text">Enter the invite code to join an existing game</p>
      
      <form onSubmit={handleSubmit}>
        <div className="input-group">
          <label htmlFor="game-code">Game Code</label>
          <input
            id="game-code"
            type="text"
            value={gameCode}
            onChange={(e) => {
              setGameCode(e.target.value);
              if (error) setError(null);
            }}
            placeholder="Enter game code"
            autoFocus
          />
        </div>
        
        {error && <div className="error-message">{error}</div>}
        
        <div className="buttons">
          <button type="button" className="btn-secondary" onClick={onCancel}>
            Cancel
          </button>
          <button type="submit" className="btn-primary">
            Join Game
          </button>
        </div>
      </form>
    </div>
  );
}