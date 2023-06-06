import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import "./EditBio.css";

function EditBio() {
  const [newBio, setNewBio] = useState("");

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Update the API endpoint and add withCredentials
      await axios.post(
        "http://localhost:8080/api/auth/updateBio",
        { newBio },
        { withCredentials: true }
      );
      alert("Bio updated successfully");
      navigate("/UserProfile");
    } catch (error) {
      console.log(error);
      alert("An error occurred while updating the bio");
    }
  };
  
  return (
<div className="box">
  <h2>Edit Bio</h2>
  <form onSubmit={handleSubmit}>
    <div className="textarea-container">
      <textarea
        id="newBio"
        name="newBio"
        rows="4"
        cols="50"
        value={newBio}
        onChange={(e) => setNewBio(e.target.value)}
        className="circular-textarea"
        placeholder="Type here..."
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

export default EditBio;
