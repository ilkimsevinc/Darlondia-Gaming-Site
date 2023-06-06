import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import { RiUserFollowLine, RiUserUnfollowLine } from "react-icons/ri";

const UserDetail = () => {
  const { username } = useParams();
  const [user, setUser] = useState({});
  const [reviews, setReviews] = useState([]);
  const [isFollowing, setIsFollowing] = useState(false);
  const [currentUser, setCurrentUser] = useState({});

  useEffect(() => {
    const fetchUserProfile = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/auth/userProfile/${username}`);
        setUser(response.data);
        setReviews(response.data.reviews);
        const currentUserId = JSON.parse(sessionStorage.getItem("authenticatedUser"))?.id;
        setCurrentUser({ id: currentUserId });
      } catch (error) {
        console.log(error);
      }
    };

    fetchUserProfile();
  }, [username]);

  useEffect(() => {
    if (user.followers) {
      const currentUserId = JSON.parse(sessionStorage.getItem("authenticatedUser"))?.id;
      setIsFollowing(user.followers.includes(currentUserId));
    }
  }, [user]);

  const toggleFollow = async () => {
    const authenticatedUser = sessionStorage.getItem('authenticatedUser');
    const currentUserId = authenticatedUser ? JSON.parse(authenticatedUser).id : null;

    if (!currentUserId) {
      console.log('User is not authenticated');
      return;
    }

    try {
      const action = isFollowing ? 'unfollow' : 'follow';
      await axios.post(`http://localhost:8080/api/auth/${action}/${user.id}`, null, { withCredentials: true });
      setIsFollowing(!isFollowing);
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <div className="container">
      <div className="details-box">
        <h1>{user.username} 's Profile</h1>
        <img src={user.username ? `http://localhost:8080/api/auth/downloadProfilePicture/${user.username}` : '/Images/Default_pfp.svg.png'} alt="Profile Picture" />
        <p>Bio: {user.bio}</p>
        <p>Account creation date: {user.accountCreationDate}</p>
        {currentUser.id !== user.id && currentUser.id && (
          <>
<button onClick={toggleFollow}>
  {isFollowing ? (
    <>
      <RiUserFollowLine />
      Follow
    </>
  ) : (
    <>
      <RiUserUnfollowLine />
      Unfollow
    </>
  )}
</button>

            {currentUser.id && !isFollowing && (
              <p></p>
            )}
          </>
        )}
      </div>
      <div className="reviews-box">
        <h2>Reviews</h2>
        <div className="reviews">
          {reviews.map((review) => (
            <div key={review.id} className="review">
              <h3>Game Name: {review.gameName}</h3>
              <p>Rating: {review.rating}</p>
              <p>{review.content}</p>
              <p>Created at: {review.createdAt}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default UserDetail;
