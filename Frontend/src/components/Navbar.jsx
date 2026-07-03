import "./Navbar.css";

export default function Navbar({ user, isLoggedIn, isGuest, onLoginClick, onRegisterClick, onProfileClick, onLogout }) {
  const userId = user?.userID ?? user?.id;

  return (
    <nav className="navbar">
      <div className="nav-left">
        Chess App
      </div>
      <div className="nav-right">
        {isLoggedIn && !isGuest && (
          <>
            <div className="nav-user">
              <span className="nav-username">{user.username}</span>
              <span className="nav-user-id">ID: {userId}</span>
            </div>
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
