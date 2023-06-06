import { useState } from "react";
import axios from "axios";

function EditProfileDetails({ user, setUser }) {
  const [loggedIn, setLoggedIn] = useState(false);
  const [formData, setFormData] = useState({
    username: user.username,
    bio: user.bio,
    profilePicture: user.profilePicture,
    accountCreationDate: user.accountCreationDate,
  });

  const handleChange = (event) => {
    setLoggedIn(true);
    const { name, value } = event.target;
    setFormData((prevFormData) => ({ ...prevFormData, [name]: value }));
  };

  const handleSubmit = async (event) => {
    setLoggedIn(true);
    event.preventDefault();
    try {
      const response = await axios.put(`http://localhost:8080/api/auth/user${user.id}`, formData);
      setUser(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  if (!loggedIn) {
    return (
      <div>
        <h2>Please log in to see your profile</h2>
        <button
          className="button"
          onClick={() => (window.location.href = "/login")}
        >
          Log in
        </button>
      </div>
    );
  }

  return (
    <div className="box">
      <h2>Edit Profile Details</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Username:
          <input
            type="text"
            name="username"
            value={formData.username}
            onChange={handleChange}
          />
        </label>
        <br />
        <label>
          Bio:
          <textarea name="bio" value={formData.bio} onChange={handleChange} />
        </label>
        <br />
        <label>
          Profile Picture URL:
          <input
            type="text"
            name="profilePicture"
            value={formData.profilePicture}
            onChange={handleChange}
          />
        </label>
        <br />
        <label>
          Account Creation Date:
          <input
            type="text"
            name="accountCreationDate"
            value={formData.accountCreationDate}
            onChange={handleChange}
          />
        </label>
        <br />
        <br />
        <button type="submit">Save Changes</button>
        <Link to="/UserProfile">
          <button>Cancel</button>
        </Link>
      </form>
    </div>
  );
}

export default EditProfileDetails;
