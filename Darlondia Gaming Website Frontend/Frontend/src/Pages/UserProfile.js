import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import "./UserProfile.css";

function UserProfile() {
  const [userProfile, setUserProfile] = useState({});
  const [reviews, setReviews] = useState([]);
  const [gameLists, setGameLists] = useState([]);
  const [followers, setFollowers] = useState([]);
  const [following, setFollowing] = useState([]);
  const [gameNames, setGameNames] = useState({});
  const authenticatedUser = sessionStorage.getItem("authenticatedUser");

  useEffect(() => {
    if (!authenticatedUser) {
      window.location.href = "/Login";
      return;
    }

    const username = JSON.parse(authenticatedUser).username;

    const fetchUserProfile = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/auth/userProfile/${username}`
        );
        setUserProfile({
          username: response.data.username,
          bio: response.data.bio,
          profilePicture: response.data.profilePicture,
          accountCreationDate: response.data.accountCreationDate,
        });
        setReviews(response.data.reviews);
        setGameLists(response.data.gameLists);
        setFollowers(response.data.followers);
        setFollowing(response.data.following);
      } catch (error) {
        console.error("Error fetching user profile:", error);
      }
    };

    fetchUserProfile();
  }, [authenticatedUser]);

  return (
    <div className="container">
      {userProfile.username ? (
        <>
          <div className="details-box">
            <h1>{userProfile.username}'s Profile</h1>
            {userProfile.username && (
              <img
                src={`http://localhost:8080/api/auth/downloadProfilePicture/${userProfile.username}`}
                alt={`${userProfile.username}'s profile`}
              />
            )}
            <p>Bio: {userProfile.bio}</p>
            <p>Account Creation Date: {userProfile.accountCreationDate}</p>
            <Link to="/EditBio">
              <button>Edit Bio</button>
            </Link>
            <Link to="/EditEmail">
              <button>Edit Email</button>
            </Link>
            <Link to="/EditUsername">
              <button>Edit Username</button>
            </Link>
            <Link to="/EditProfilePicture">
              <button>Edit Profile Picture</button>
            </Link>
            <Link to="/CreateList">
              <button>Create List</button>
            </Link>
          </div>
          <div className="reviews-box">
            <h2>Followers</h2>
            <ul>
              {followers.map((follower, index) => (
                <Link to={`/user/${follower}`} key={index}>
                  <p>{follower}</p>
                </Link>
              ))}
            </ul>

            <h2>Following</h2>
            <ul>
              {following.map((follow, index) => (
                <Link to={`/user/${follow}`} key={index}>
                  <p>{follow}</p>
                </Link>
              ))}
            </ul>
            <h2>Game Lists</h2>
            {gameLists.length === 0 ? (
              <p>No lists</p>
            ) : (
              <ul>
                {gameLists.map((gameList) => (
                  <p key={gameList.id}>
                    <Link to={`/lists/${gameList.id}`}>
                      <h3>{gameList.name}</h3>
                    </Link>
                    <ul>
                      {gameList.games.map((game) => (
                        <p key={game.id}>
                          <Link to={`/game/${game.id}`}>{game.name}</Link>
                        </p>
                      ))}
                    </ul>
                  </p>
                ))}
              </ul>
            )}
          </div>

          <div className="reviews-box">
            <h2>Reviews</h2>
            <ul>
              {reviews.map((review) => (
                <p key={review.reviewId}>
                  <h3>Game Name: {review.gameName}</h3>{" "}
                  {/* Update review.gameName to gameNames[review.reviewId] */}
                  <p>Rating: {review.rating}</p>
                  <p>{review.content}</p>
                  <p>Created at: {review.createdAt}</p>
                </p>
              ))}
            </ul>
          </div>
        </>
      ) : (
        <div>Loading...</div>
      )}
    </div>
  );
}

export default UserProfile;
