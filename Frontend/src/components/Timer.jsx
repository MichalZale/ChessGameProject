import React from 'react';
import './Timer.css';

const formatTime = (totalSeconds) => {
  if (isNaN(totalSeconds) || totalSeconds < 0) {
    totalSeconds = 0;
  }
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
};

export default function Timer({ label, timeInSeconds, isActive }) {
  return (
    <div className={`timer ${isActive ? 'active' : ''}`}>
      <div className="timer-label">{label}</div>
      <div className="time-display">{formatTime(timeInSeconds)}</div>
    </div>
  );
}