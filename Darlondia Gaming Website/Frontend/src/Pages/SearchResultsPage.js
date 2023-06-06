import React from "react";
import { useLocation, Link } from "react-router-dom";

const SearchResultsPage = () => {
  const location = useLocation();
  const { searchResults } = location.state;

  return (
    <div>
      <h2>Search Results</h2>
      <ul>
        {searchResults.map((game) => (
          <li key={game.name} className="game-item">
            <Link to={`/games/${game.id}`}>
            <img
                src={`http://localhost:8080/api/games/image/${game.pictureUrl}`}
                
              />
            <div className="game-name-overlay">{game.name}</div></Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default SearchResultsPage;