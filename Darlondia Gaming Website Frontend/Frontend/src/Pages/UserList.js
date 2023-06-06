import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

function UserList() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/auth/usernames");
        const sortedUsers = response.data.sort((a, b) => a.localeCompare(b)); // Sort usernames alphabetically
        setUsers(sortedUsers);
      } catch (error) {
        console.log(error);
      }
    };

    fetchUsers();
  }, []);

  return (
    <div>
      <h1>User List</h1>
      <ul className="game-list">
        {users.map((username) => (
          <li key={username} className="game-item">
            <Link to={`/user/${username}`}>
              <img
                src={`http://localhost:8080/api/auth/downloadProfilePicture/${username}`}
                alt={username}
              />
              <div className="game-name-overlay">{username}</div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
  
  
}

export default UserList;
