import { useState } from "react";
import { register } from "../API/userAPI";
import "./AuthForm.css";

export default function RegisterForm({ onSuccess, onCancel }) {
  const [step, setStep] = useState(1);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  function next() { setStep(step + 1); setError(null); }
  function back() { setStep(step - 1); setError(null); }

  async function handleSubmit(e) {
    e.preventDefault();
    if (step < 3) {
      next();
      return;
    }

    if (!username || !password || !email) {
      setError("All fields are required.");
      return;
    }

    setIsLoading(true);
    setError(null);

    console.log('Form submitted with values:', { username, password: '***', email });
    
    try {
      const user = await register(username, password, email);
      console.log('Registration successful, user data:', user);
      onSuccess(user);
    } catch (err) {
      console.error('Registration error in form:', err);
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <h2>Register</h2>

      {step === 1 && (
        <>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
            disabled={isLoading}
          />
          <div className="auth-buttons">
            <button type="submit" disabled={isLoading}>Next</button>
            <button type="button" onClick={onCancel} disabled={isLoading}>Back</button>
          </div>
        </>
      )}

      {step === 2 && (
        <>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            disabled={isLoading}
          />
          <div className="auth-buttons">
            <button type="submit" disabled={isLoading}>Next</button>
            <button type="button" onClick={back} disabled={isLoading}>Back</button>
          </div>
        </>
      )}

      {step === 3 && (
        <>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            disabled={isLoading}
          />
          {error && <div className="auth-error">{error}</div>}
          <div className="auth-buttons">
            <button type="submit" disabled={isLoading}>
              {isLoading ? "Registering..." : "Register"}
            </button>
            <button type="button" onClick={back} disabled={isLoading}>Back</button>
          </div>
        </>
      )}
    </form>
  );
}
