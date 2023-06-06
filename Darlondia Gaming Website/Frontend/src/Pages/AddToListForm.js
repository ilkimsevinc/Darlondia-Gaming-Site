import React, { useState, useEffect } from "react";
import axios from 'axios';
import { useParams, Link, useNavigate } from "react-router-dom";

const AddToListForm = () => {
  const { gameId } = useParams();
  const [listId, setListId] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [lists, setLists] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const authenticatedUser = sessionStorage.getItem('authenticatedUser');
    const username = JSON.parse(authenticatedUser).username;
    axios.get(`http://localhost:8080/api/auth/userProfile/${username}/gameLists`, { withCredentials: true })
      .then((response) => {
        setLists(response.data);
      })
      .catch((error) => {
        console.error('Error fetching user lists:', error);
      });
  }, []);

  const handleListSelectChange = (event) => {
    setListId(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const selectedList = lists.find((list) => list.id === listId);
    if (!selectedList) {
      setErrorMessage("Please select a valid list.");
      return;
    }

    axios.post(
      `http://localhost:8080/api/auth/gameList/${selectedList.id}/addGame/${gameId}`,
      {},
      { withCredentials: true }
    )
      .then(() => {
         alert("Game added successfully to the list");
         navigate(`/games/${gameId}`);

      })
      .catch((error) => {
        console.error('Error adding game to list:', error);
        if (error.response && error.response.data === "Game is already in the list or error adding the game to the list") {
          setErrorMessage("This game is already in the selected list.");
        } else {
          setErrorMessage("Could not add game to list. Please try again.");
        }
      });

  };

  return (
    <div className="box">
      <h2>Select A List</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="listSelect">Select a List:</label>
          <select id="listSelect" value={listId} onChange={handleListSelectChange} required>
            <option value="" style={{ fontStyle: 'italic', color: 'gray' }}>Pick a List...</option>
            {lists.map((list) => (
              <option key={list.id} value={list.id}>
                {list.name}
              </option>
            ))}
          </select>
        </div>
        <button type="submit">Add to List</button>
        <Link to="/UserProfile">
          <button className="cancel-button">Cancel</button>
        </Link>
      </form>
    </div>
  );     
};

export default AddToListForm;
