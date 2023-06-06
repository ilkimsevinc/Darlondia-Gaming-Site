import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import "./EditUsername.css";

function EditUsername() {
  const [newUsername, setNewUsername] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Update the API endpoint and add withCredentials
      await axios.post(
        "http://localhost:8080/api/auth/changeUsername",
        { newUsername },
        { withCredentials: true }
      );
      alert("Username updated successfully");
      navigate("/UserProfile");
    } catch (error) {
      console.log(error);
      alert("An error occurred while updating the bio");
    }
  };

  return (
    <div className="box">
    <h2>Edit Username</h2>
    <form onSubmit={handleSubmit}>
      <div>
        <input
          type="text"
          id="newUsername"
          name="newUsername"
          value={newUsername}
          onChange={(e) => setNewUsername(e.target.value)}
          placeholder="Enter username..."
        />
      </div>
      <button type="submit" className="submit-button">Save Changes</button>
      <Link to="/UserProfile">
        <button className="cancel-button">Cancel</button>
      </Link>
    </form>
  </div>
);
}

export default EditUsername;
