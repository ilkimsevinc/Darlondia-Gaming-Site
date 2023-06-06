import React, { useState } from "react";
import { BiSearch } from 'react-icons/bi';
import "./SearchBar.css";

function SearchBar({ onSearch }) {
  const [searchTerm, setSearchTerm] = useState("");

  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleSearchSubmit = (event) => {
    event.preventDefault();
    onSearch(searchTerm);
  };

  return (
    <form onSubmit={handleSearchSubmit} className="search-bar-container">
      <input
        type="text"
        placeholder="Search for a game..."
        value={searchTerm}
        onChange={handleSearchChange}
      />
      <button type="search-bar-button">
        <BiSearch />
      </button>
    </form>
  );
}

export default SearchBar;
