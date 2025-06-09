import "./Navbar.css";

export default function Navbar({ isLoggedIn, isGuest, onLoginClick, onRegisterClick, onProfileClick, onLogout }) {
  return (
    <nav className="navbar">
      <div className="nav-left">
        Chess App
      </div>
      <div className="nav-right">
        {isLoggedIn && !isGuest && (
          <>
            <button className="nav-btn" onClick={onProfileClick}>
              Profile
            </button>
            {onLogout && (
              <button className="nav-btn" onClick={onLogout}>
                Logout
              </button>
            )}
          </>
        )}
        
        {(!isLoggedIn || isGuest) && (
          <>
            <button className="nav-btn" onClick={onLoginClick}>
              Login
            </button>
            <button className="nav-btn" onClick={onRegisterClick}>
              Register
            </button>
          </>
        )}
      </div>
    </nav>
  );
}

