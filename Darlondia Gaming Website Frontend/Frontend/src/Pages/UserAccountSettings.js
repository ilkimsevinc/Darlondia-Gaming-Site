import { useState } from "react";
import axios from "axios";

function EditProfileDetails({ user, setUser }) {
const [loggedIn, setLoggedIn] = useState(false);
  const [formData, setFormData] = useState({
    username: user.username,
    email: user.email,
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
      const response = await axios.put(`/api/users/${user.id}`, formData);
      setUser(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  if (!loggedIn) {
    return (
      <div>
        <h2>Please log in to see your account settings</h2>
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
      <h2>Edit Account Settings</h2>
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
          Email:
          <textarea
            name="email"
            value={formData.email}
            onChange={handleChange}
          />
        </label>
        <a href="/resetpassword">
          <div className="forgotPassword">Change password?</div>
        </a>
        <br />
        <br />
        <label>Account Creation Date: {user.accountCreationDate}</label>
        <br />
        <br />
        <button type="submit">Save Changes</button>
        <Link to="/UserAccountSettings">
          <button>Cancel</button>
        </Link>
      </form>
    </div>
  );
}

export default EditProfileDetails;
