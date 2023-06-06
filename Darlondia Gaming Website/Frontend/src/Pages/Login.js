import React, { useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import "./Login.css";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [status, setStatus] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/login",
        { username, password },
        { withCredentials: true }
      );
      sessionStorage.setItem(
        "authenticatedUser",
        JSON.stringify(response.data)
      );
      const user = response.data.username;

      if (user) {
        setStatus("Logged in successfully.");
        navigate("/");
        window.location.reload();
      } else {
        setStatus("Logged in, but user information is not available.");
      }
    } catch (error) {
      console.log(error);
      setStatus("Cannot login.");
    }
  };

  return (
    <div className="centered">
      <div className="login-box">
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="user-box">
            <input
              type="text"
              name="username"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <label htmlFor="username">Username</label>
          </div>
          <div className="user-box">
            <input
              type="password"
              name="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <label htmlFor="password">Password</label>
          </div>
          <div className="form-row">
            <label>
              <input type="checkbox" />
              Remember me?
            </label>
            {status && <div className="status">{status}</div>}
            <Link to="/forgotpassword">
              <div className="forgotPassword">Forgot password?</div>
            </Link>
          </div>
          <button type="submit">Login</button>
          <button className="not-registered-button">
            <Link to="/registerenduser" className="not-registered-link">
              Not Registered?
            </Link>
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;
