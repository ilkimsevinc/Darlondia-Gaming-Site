import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Axios from "axios";
import "./ForgotPassword.css";

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const sendResetCode = () => {
    setError("");
    setStatus("");

    if (!email) {
      setError("Please enter your email.");
      return;
    }

    Axios.post("http://localhost:8080/api/auth/forgot-password", {
      email: email,
    })
      .then((response) => {
        setStatus(response.data.message);
        setTimeout(() => {
          navigate("/resetpassword");
        }, 2000);
      })
      .catch((error) => {
        console.log(error);
        setError(
          "An error occurred while sending the reset code. Please try again later."
        );
      });
  };

  return (
    <div className="centered">
      <div className="box">
        <br></br>
        <h2>Forgot Password</h2>
        <br></br>
        <input
          type="text"
          placeholder="Enter your email..."
          onChange={(e) => setEmail(e.target.value)}
        />
        <button className="button" onClick={sendResetCode}>
          <span>Send Code</span>
        </button>
        <br></br>
        <br></br>
      </div>
    </div>
  );
}

export default ForgotPassword;
