import './Navbar.css';

export default function Navbar({ isLoggedIn, onLoginClick, onRegisterClick, onProfileClick }) {
  return (
    <nav className="navbar">
      <div className="nav-left">ReactChess</div>
      <div className="nav-right">
        {isLoggedIn ? (
          <button className="nav-btn" onClick={onProfileClick}>Profile</button>
        ) : (
          <>
            <button className="nav-btn" onClick={onLoginClick}>Log in</button>
            <button className="nav-btn" onClick={onRegisterClick}>Register</button>
          </>
        )}
      </div>
    </nav>
  );
}

