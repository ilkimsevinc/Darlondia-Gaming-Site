import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Link } from 'react-router-dom';

const ListDetails = () => {
  const { id } = useParams();
  const [list, setList] = useState(null);

  useEffect(() => {
    const fetchListDetails = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/auth/lists/${id}`);
        setList(response.data);
      } catch (error) {
        console.log(error);
      }
    };

    fetchListDetails();
  }, [id]);

  if (!list) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>List Name: {list.gameListName}</h1>
      <h2>Games:</h2>
      <ul>
        {list.games.map((game) => (
          <li key={game.gameId}>
            <Link to={`/games/${game.gameId}`}>{game.gameName}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ListDetails;