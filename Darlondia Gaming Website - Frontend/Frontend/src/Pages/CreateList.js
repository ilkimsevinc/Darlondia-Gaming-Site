import React, { useState } from 'react';
import axios from 'axios';
import { Link } from "react-router-dom";
import "./CreateList.css";

function CreateList() {
  const [name, setName] = useState('');
  const authenticatedUser = sessionStorage.getItem('authenticatedUser');
  const userId = JSON.parse(authenticatedUser).id;

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      const response = await axios.post(
        `http://localhost:8080/api/auth/gameList/create?name=${name}`,
        {},
        { withCredentials: true }
      );
      window.location.href = '/UserProfile';
    } catch (error) {
      console.error('Error creating game list:', error);
    }
  };

  const handleNameChange = (event) => {
    setName(event.target.value);
  };

  return (
    <div className="box">
      <h1>Create a Game List</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Name:
          <input
            type="text"
            value={name}
            onChange={handleNameChange}
            required
            placeholder="Type a name..."
            style={{ color: "gray", fontStyle: "italic" }}
          />
        </label>
        <button type="submit">Create List</button>
        <Link to="/UserProfile">
          <button className="cancel-button">Cancel</button>
        </Link>
      </form>
    </div>
  );  
}

export default CreateList;
