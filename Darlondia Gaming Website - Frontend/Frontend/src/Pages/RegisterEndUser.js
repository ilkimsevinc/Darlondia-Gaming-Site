import { useState } from "react";
import Axios from "axios";
import { useNavigate } from "react-router-dom";
import "./RegisterEndUser.css";

function RegisterEndUser() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const validateEmail = (email) => {
    // Email validation regex
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  };

  const validatePassword = (password) => {
    // Password validation regex
    const passwordRegex = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$/;
    return passwordRegex.test(password);
  };

  const registerEndUser = () => {
    // Validate inputs
    if (!username || !email || !password || !confirmPassword) {
      setError("Please fill in all fields.");
      return;
    }

    if (!validateEmail(email)) {
      setError("Please enter a valid email address.");
      return;
    }

    if (!validatePassword(password)) {
      setError(
        "Please enter a password with at least 8 characters, including at least one uppercase letter, one lowercase letter, and one number."
      );
      return;
    }

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    if (!termsAccepted) {
      setError("Please accept the terms and conditions.");
      return;
    }

    // Make API call to register user
    Axios.post("http://localhost:8080/api/auth/register", {
      username,
      email,
      password,
      passwordConfirmation: confirmPassword,
      terms: termsAccepted,
    }).then((response) => {
      if (response.data.message) {
        setError(response.data.message);
      } else {
        setError("");
        // User successfully registered, redirect to login page
        navigate("/login");
      }
    });
  };

  return (
    <div className="centered">
      <div className="register-box">
        <h2>Register</h2>
        <div className="form-field">
          <input
            className="register-input"
            type="text"
            name="username"
            id="username"
            required
            value={username}
            onChange={(e) => {
              setUsername(e.target.value);
            }}
          />
          <label htmlFor="username">Username</label>
        </div>
        <div className="form-field">
          <input
            className="register-input"
            type="email"
            name="email"
            id="email"
            required
            value={email}
            onChange={(e) => {
              setEmail(e.target.value);
            }}
          />
          <label htmlFor="email">Email</label>
        </div>
        <div className="form-field">
          <input
            className="register-input"
            type="password"
            name="password"
            id="password"
            required
            value={password}
            onChange={(e) => {
              setPassword(e.target.value);
            }}
          />
          <label htmlFor="password">Password</label>
        </div>
        <div className="form-field">
          <input
            className="register-input"
            type="password"
            name="confirm-password"
            id="confirm-password"
            required
            value={confirmPassword}
            onChange={(e) => {
              setConfirmPassword(e.target.value);
            }}
          />
          <label htmlFor="confirm-password">Confirm Password</label>
        </div>
        <div className="form-field-checkbox">
          <input
            type="checkbox"
            name="terms-and-conditions"
            id="terms-and-conditions"
            required
            checked={termsAccepted}
            onChange={(e) => {
              setTermsAccepted(e.target.checked);
            }}
          />
          <label htmlFor="terms-and-conditions">
            I accept the terms and conditions
          </label>
        </div>
        {error && <h6 className="error">{error}</h6>}
        <a href="#" className="submit-btn" onClick={registerEndUser}>
        <button type="submit">Register</button>
        <span></span>
        <span></span>
        <span></span>
        <span></span>
        </a>
      </div>
    </div>
  );
};

export default RegisterEndUser;
