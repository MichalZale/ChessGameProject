import React, { useState } from "react";

export default function GameCreationMenu({ onReturn, onCreate }) {
  const [whiteTime, setWhiteTime] = useState(300);
  const [blackTime, setBlackTime] = useState(300);
  const [whiteInc, setWhiteInc] = useState(5);
  const [blackInc, setBlackInc] = useState(5);

  return (
    <div className="game-creation-menu">
      <h2>Game Creation</h2>
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
      <div className="button-row">
        <button onClick={onReturn}>Return</button>
        <button onClick={() => onCreate({ whiteTime, blackTime, whiteInc, blackInc })}>Create</button>
      </div>
    </div>
  );
}