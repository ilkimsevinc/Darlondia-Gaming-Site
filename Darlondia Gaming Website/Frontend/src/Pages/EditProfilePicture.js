import { useState } from "react";
import Axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import "./EditProfilePicture.css";

function UpdateProfilePicture() {
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const updateProfilePicture = () => {
    setError("");
    setStatus("");

    if (!file) {
      setError("Please select a file to upload.");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);

   Axios.post("http://localhost:8080/api/auth/uploadProfilePicture", formData, {
     headers: {
       "Content-Type": "multipart/form-data",
     },
     withCredentials: true,
   })

      .then((response) => {
        setStatus(response.data.message);
        setTimeout(() => {
          navigate("/UserProfile");
        }, 2000);
      })
      .catch((error) => {
        console.log(error);
        setError(
          "An error occurred while uploading your profile picture. Please try again later."
        );
      });
  };

  const handleFileInputChange = (event) => {
    setFile(event.target.files[0]);
  };

  return (
    <div className="centered">
      <div className="box">
        <br></br>
        <h2>Update Profile Picture</h2>
        <br></br>
        <input type="file" onChange={handleFileInputChange} />
        {error && <h6 style={{ color: "red" }}>{error}</h6>}
        {status && <h6 style={{ color: "green" }}>{status}</h6>}
        <button className="upload-button" onClick={updateProfilePicture}>
          <span>Upload</span>
        </button>
        <Link to="/UserProfile">
          <button className="cancel-button">Cancel</button>
        </Link>
        <br></br>
        <br></br>
      </div>
    </div>
  );
}

export default UpdateProfilePicture;
