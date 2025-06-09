import { useState } from "react";
import { login } from "../API/userAPI";
import "./AuthForm.css";

export default function LoginForm({ onSuccess, onCancel }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  async function handleSubmit(e) {
    e.preventDefault();
    if (!username || !password) {
      setError("Both fields are required.");
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      const user = await login(username, password);
      onSuccess(user);
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <h2>Log in</h2>

      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={e => setUsername(e.target.value)}
        required
        disabled={isLoading}
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={e => setPassword(e.target.value)}
        required
        disabled={isLoading}
      />

      {error && <div className="auth-error">{error}</div>}

      <div className="auth-buttons">
        <button type="submit" disabled={isLoading}>
          {isLoading ? "Logging in..." : "Log in"}
        </button>
        <button type="button" onClick={onCancel} disabled={isLoading}>
          Back
        </button>
      </div>
    </form>
  );
}
