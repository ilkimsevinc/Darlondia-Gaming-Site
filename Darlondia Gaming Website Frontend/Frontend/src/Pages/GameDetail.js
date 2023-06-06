import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import ReviewForm from "./ReviewForm";
import "./ReviewForm";
import "./GameDetail.css";

const GameDetail = () => {
  const { id } = useParams();
  const [game, setGame] = useState({});
  const [reviews, setReviews] = useState([]);

  useEffect(() => {
    fetch(`http://localhost:8080/api/games/get/${id}`)
      .then((response) => response.json())
      .then((data) => {
        setGame(data);
      });

    fetch(`http://localhost:8080/api/reviews/game/${id}`)
      .then((response) => response.json())
      .then((data) => {
        setReviews(data);
      });
  }, [id]);

  const handleReviewSubmitted = (newReview) => {
    if (newReview.id) {
      // Review already exists, so update the corresponding review in the state
      const updatedReviews = reviews.map((review) =>
        review.id === newReview.id ? newReview : review
      );
      setReviews(updatedReviews);
    } else {
      // Review is new, so add it to the state
      setReviews([...reviews, newReview]);
    }
  };

  return (
    <div className="container">
      <div className="details-box">
        <h1>{game.name}</h1>
        {game.pictureUrl && (
          <img
            src={`http://localhost:8080/api/games/image/${game.pictureUrl}`}
            alt={game.name}
          />
        )}
        <p>Release Date: {game.releaseDate}</p>
        <p>Platform: {game.platform}</p>
        <p>Genre: {game.genre}</p>
        <p>Description: {game.description}</p>
        <Link to={`/AddToListForm/${id}`}>
          <button>Add to List</button>
        </Link>
      </div>
      <div className="reviews-box">
        <h2>Write a Review:</h2>
        <ReviewForm
          gameId={id}
          initialReview={null}
          onReviewSubmitted={handleReviewSubmitted}
        />
        <h2>Reviews:</h2>
        {reviews && reviews.length > 0 ? (
          <ul>
            {reviews.map((review) => (
              <p key={review.id}>
                <p>
                  <strong>
                    {review.username ? review.username : "Unknown user"}:
                  </strong>{" "}
                  {review.content}
                </p>
                <p>Rating: {review.rating}</p>
              </p>
            ))}
          </ul>
        ) : (
          <p>No reviews available.</p>
        )}
      </div>
    </div>
  );
};

export default GameDetail;
