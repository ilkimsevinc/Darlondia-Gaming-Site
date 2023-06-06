import { Navbar, Nav, Container, NavDropdown } from "react-bootstrap";
import SearchBar from "./SearchBar";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import React, { useState } from "react";
import { FiLogOut, FiUsers, FiUser,FiLogIn } from "react-icons/fi";
import { CgGames } from "react-icons/cg";
import { TiThListOutline } from "react-icons/ti";
import { CgUserList } from 'react-icons/cg';
import { IoKeyOutline } from 'react-icons/io5';
import { HiOutlinePencilAlt } from 'react-icons/hi';

const Header = () => {
  const authenticatedUser = sessionStorage.getItem("authenticatedUser");
  const isAuthenticated = !!authenticatedUser;
  const navigate = useNavigate();
  const [searchResults, setSearchResults] = useState([]);

  const handleSearch = async (searchTerm) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/games/search?name=${searchTerm}`,
        { withCredentials: true }
      );
      console.log("Input:", searchTerm);
      setSearchResults(response.data);
      console.log("Response:", response.data);
      navigate(`/search-results`, { state: { searchResults: response.data } });
    } catch (error) {
      console.log("Search error:", error);
    }
  };

  const handleLogout = async () => {
    try {
      await axios.post(
        "http://localhost:8080/api/auth/logout",
        {},
        { withCredentials: true }
      );
      sessionStorage.removeItem("authenticatedUser");
      window.location.href = "/login";
      window.location.reload();
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div>
      <Navbar collapseOnSelect expand="lg" style={{ backgroundColor: "#2D033B" }} variant="dark">
        
          <Navbar.Brand href="/" className="neon">
            Darlondia Gaming Site
          </Navbar.Brand>
          <Navbar.Toggle aria-controls="responsive-navbar-nav" />
          <Navbar.Collapse id="responsive-navbar-nav">
            <Nav className="me-auto">
              <div className="search-container">
                <SearchBar onSearch={handleSearch} />
              </div>
            </Nav>
            <Nav>
              <Nav.Link href="/GameList">
                 <CgGames size={18} /> Games
              </Nav.Link>
              <Nav.Link href="/AllLists">
                 <TiThListOutline size={18} /> All Lists
              </Nav.Link>
              <Nav.Link href="/UserList">
                <CgUserList size={19} /> User List
              </Nav.Link>
              <Nav.Link href="/ForgotPassword">
                <IoKeyOutline size={18} /> Forgot Password
              </Nav.Link>
              {isAuthenticated ? (
                <>
                  <Nav.Link href="/UserProfile">
                    <FiUser size={18} /> User Profile
                  </Nav.Link>
                  <Nav.Link onClick={handleLogout}>
                    <FiLogOut size={18} /> Log Out
                  </Nav.Link>
                </>
              ) : (
                <>
                  <Nav.Link href="/RegisterEndUser">
                     <HiOutlinePencilAlt size={18} /> Register
                        </Nav.Link>
                  <Nav.Link href="/Login"> 
                  <FiLogIn size={18} /> Login
                  </Nav.Link>
                </>
              )}
            </Nav>
          </Navbar.Collapse>
        
      </Navbar>
    </div>
  );
};

export default Header;
