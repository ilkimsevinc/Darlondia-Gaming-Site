import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import "./AllLists.css";

const AllLists = () => {
  const [lists, setLists] = useState([]);

  useEffect(() => {
    const fetchLists = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/auth/users/lists');
        setLists(response.data);
      } catch (error) {
        console.log(error);
      }
    };
    fetchLists();
  }, []);

  return (
    <div className="all-lists-container">
      <h1>All Lists</h1>
      <ul className="game-list">
        {lists.map((list) => (
          <li key={list.id} className="game-item">
            <Link to={`/lists/${list.id}`}>
              <div className="list-item-overlay">{list.name}</div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
  
};

export default AllLists;
