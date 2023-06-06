import React, { useEffect, useState } from "react";
import axios from "axios";

function LogOut() {
  const [user, setUser] = useState(null);
  const checkLoginStatus = async () => {
    try {
      // Make a GET request to the server-side API to check login status
      const response = await Axios.get("/Login");
      setUser(response.data);
    } catch (error) {
      console.error("Failed to check login status:", error);
    }
  };

  if(!user){
    window.location.href = "/Login";
    }else{
    const handleLogout = async () => {
        await axios.post("http://localhost:8080/api/auth/logout", {}, { withCredentials: true });
        sessionStorage.removeItem("authenticatedUser");
        window.location.href = "/Login";
    };
  
    return (
      <div>
        {isLoggedIn ? (
          <button onClick={handleLogout}>
            Logout
          </button>
        ) : (
          <div>
            <p>You are not logged in</p>
            {/* Display error message if not logged in */}
          </div>
        )}
      </div>
    );
  };
}

export default LogOut;
