import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

function UserGameLists() {
  const [user, setUser] = useState(null);
  const [lists, setLists] = useState([]);
  const [newListName, setNewListName] = useState("");
  const [newGameName, setNewGameName] = useState("");
  
  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/auth/profile",
          { withCredentials: true }
        );
        setUser(response.data);
        setLists(response.data.gameLists);
      } catch (error) {
        if (error.response.status === 401) {
          window.location.href = "/Login";
        } else {
          console.log(error);
        }
      }
    };

    fetchUserData();
  }, []);

  const handleCreateList = async (event) => {
    event.preventDefault();
    try {
      const response = await axios.post(
        "http://localhost:8080/api/game-lists",
        { name: newListName },
        { withCredentials: true }
      );
      setLists([...lists, response.data]);
      setNewListName("");
    } catch (error) {
      console.log(error);
    }
  };

  const handleDeleteList = async (listId) => {
    try {
      await axios.delete(`http://localhost:8080/api/game-lists/${listId}`, {
        withCredentials: true,
      });
      setLists(lists.filter((list) => list.id !== listId));
    } catch (error) {
      console.log(error);
    }
  };

  const handleAddGameToList = async (listId, gameId) => {
    try {
      const response = await axios.post(
        `http://localhost:8080/api/game-lists/${listId}/games`,
        { gameId },
        { withCredentials: true }
      );
      setLists(
        lists.map((list) =>
          list.id === listId
            ? { ...list, games: [...list.games, response.data] }
            : list
        )
      );
    } catch (error) {
      console.log(error);
    }
  };

  
  const handleRenameList = async (listId) => {
    try {
      const newName = prompt("Enter the new name for the list");
      if (newName) {
        const response = await axios.put(
          `http://localhost:8080/api/game-lists/${listId}`,
          { name: newName },
          { withCredentials: true }
        );
        setLists(
          lists.map((list) =>
            list.id === listId ? { ...list, name: response.data.name } : list
          )
        );
      }
    } catch (error) {
      console.log(error);
    }
  };

  const handleDeleteGameFromList = async (listId, gameId) => {
    try {
      await axios.delete(
        `http://localhost:8080/api/game-lists/${listId}/games/${gameId}`,
        { withCredentials: true }
      );
      setLists(
        lists.map((list) =>
          list.id === listId
            ? { ...list, games: list.games.filter((game) => game.id !== gameId) }
            : list
        )
      );
    } catch (error) {
      console.log(error);
    }
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="box">
      <h1>Game Lists</h1>
      {lists.length === 0 ? (
        <div>No lists found. Create a new one!</div>
      ) : (
        <div className="lists-container">
          {lists.map((list) => (
            <div key={list.id} className="list">
              <div className="list-header">
                <h2>{list.name}</h2>
                <div className="list-header-buttons">
                  <button onClick={() => handleRenameList(list.id)}>
                    Rename
                  </button>
                  <button onClick={() => handleDeleteList(list.id)}>
                    Delete
                  </button>
                </div>
              </div>
              <div className="list-games">
                <h3>Games:</h3>
                {list.games.length === 0 ? (
                  <div>No games found. Add a new one!</div>
                ) : (
                  <ul>
                    {list.games.map((game) => (
                      <li key={game.id}>
                        {game.name}{" "}
                        <button onClick={() => handleDeleteGameFromList(list.id, game.id)}>
                          Remove
                        </button>
                      </li>
                    ))}
                  </ul>
                )}
                <div className="add-game-form">
                  <h4>Add a game to this list:</h4>
                  <input
                    type="text"
                    value={newGameName}
                    onChange={(e) => setNewGameName(e.target.value)}
                    placeholder="Enter game name"
                  />
                  <button onClick={() => handleAddGameToList(list.id)}>Add</button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
      <div className="add-list-form">
        <h2>Create a new list:</h2>
        <input
          type="text"
          value={newListName}
          onChange={(e) => setNewListName(e.target.value)}
          placeholder="Enter list name"
        />
        <button onClick={handleCreateList}>Create</button>
      </div>
    </div>
  );
}
export default UserGameLists;