import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import "./EditEmail.css";

function EditEmail() {
  const [newEmail, setNewEmail] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Update the API endpoint and add withCredentials
      await axios.post(
        "http://localhost:8080/api/auth/changeEmail",
        { newEmail },
        { withCredentials: true }
      );
      alert("Email updated successfully");
      navigate("/UserProfile");
    } catch (error) {
      console.log(error);
      alert("An error occurred while updating the Email");
    }
  };

  return (
<div className="box">
  <h2>Edit Email</h2>
  <form onSubmit={handleSubmit}>
    <div>
      <input
        id="newEmail"
        name="newEmail"
        type="text"
        value={newEmail}
        onChange={(e) => setNewEmail(e.target.value)}
        placeholder="Enter email..."
        className="input-field"
      />
    </div>
    <button type="submit">Save Changes</button>
    <Link to="/UserProfile">
      <button className="cancel-button">Cancel</button>
    </Link>
  </form>
</div>
  );
}

export default EditEmail;
