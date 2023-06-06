import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import "./Home.css";

function Home() {
  const [games, setGames] = useState([]);

  useEffect(() => {
    const fetchGames = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/games/list"
        );
        const sortedGames = response.data.sort((a, b) =>
          a.name.localeCompare(b.name)
        ); // Sort games alphabetically by name
        setGames(sortedGames);
      } catch (error) {
        console.log(error);
      }
    };

    fetchGames();
  }, []);

  return (
    <div>
      <h1>Welcome to Darlondia Gaming Site</h1>
      <ul className="game-list">
        {games.map((game) => (
          <li key={game.id} className="game-item">
            <Link to={`/games/${game.id}`}>
              <img
                src={`http://localhost:8080/api/games/image/${game.pictureUrl}`}
                
              />
              <div className="game-name-overlay">{game.name}</div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Home;
