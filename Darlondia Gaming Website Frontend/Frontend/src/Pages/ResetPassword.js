import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function ResetPassword() {
  const [code, setCode] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [status, setStatus] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const resetPassword = () => {
    setError("");
    setStatus("");

    if (!code || !password || !confirmPassword) {
      setError("Please fill in all fields.");
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

    axios
      .post("http://localhost:8080/api/auth/reset-password", {
        resetCode: code,
        newPassword: password,
        confirmPassword: confirmPassword,
      })
      .then((response) => {
        setStatus(response.data);
      })
      .catch((error) => {
        console.log(error);
        setError(
          "An error occurred while resetting password. Please try again later."
        );
      });
  };

  const validatePassword = (password) => {
    const re = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[0-9a-zA-Z!@#$%^&*]{8,}$/;

    return re.test(password);
  };

  return (
    <div className="centered">
      <div className="box">
        <br></br>
                <h2>Reset Password</h2>
                <br></br>
                <input
                  type="text"
                  placeholder="Enter the code from your email"
                  onChange={(e) => setCode(e.target.value)}
                />
                <input
                  type="password"
                  placeholder="New password"
                  onChange={(e) => setPassword(e.target.value)}
                />
                <input
                  type="password"
                  placeholder="Confirm your password"
                  onChange={(e) => setConfirmPassword(e.target.value)}
                />
                {error && <h6 style={{ color: "red" }}>{error}</h6>}
                {status && <h6 style={{ color: "green" }}>{status}</h6>}
                <button className="button" onClick={resetPassword}>
                  <span>Reset Password</span>
                </button>
                <br></br>
                <br></br>
              </div>
            </div>
          );
        }

        export default ResetPassword;

