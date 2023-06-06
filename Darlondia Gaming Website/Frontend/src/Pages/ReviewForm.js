import React, { useState } from "react";
import axios from "axios";
import "./ReviewForm.css";
import { Link } from "react-router-dom";

const ReviewForm = ({ gameId, initialReview = null, onReviewSubmitted }) => {
  const [content, setContent] = useState(initialReview ? initialReview.content : "");
  const [rating, setRating] = useState(initialReview ? initialReview.rating : 1);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(
        `http://localhost:8080/api/games/${gameId}/reviews/create`,
        { content: content, rating: rating },
        { withCredentials: true }
      );
      setContent("");
      setRating(1);
      onReviewSubmitted(response.data); // inform parent component that a new review has been submitted
      window.location.reload();
    } catch (error) {
      console.log(error);
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      await axios.put(
        `http://localhost:8080/api/games/${gameId}/reviews/update/${initialReview.id}`,
        { content: content, rating: rating },
        { withCredentials: true }
      );
      onReviewSubmitted(); // inform parent component that the review has been updated
    } catch (error) {
      console.log(error);
    }
  };

  const handleDelete = async (e) => {
    e.preventDefault();
    try {
      await axios.delete(
        `http://localhost:8080/api/games/${gameId}/reviews/delete/${initialReview.id}`,
        { withCredentials: true }
      );
      onReviewSubmitted(); // inform parent component that the review has been deleted
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <form onSubmit={initialReview ? handleUpdate : handleSubmit}>
      <div>
        <label htmlFor="content">Content:</label>
        <textarea
  id="content"
  value={content}
  onChange={(e) => setContent(e.target.value)}
  style={{
    height: "100px",
    width: "100%",
    color: "gray",
    fontStyle: "italic"
  }}
  placeholder="Write a review, tell others what you like and don't like! For example: This game will win Game of The Year award 100 percent!! or Do not buy this game, I would rather watch paint dry... "
/>

      </div>

      <div>
        <label htmlFor="rating">Rating:</label>
        <select
          id="rating"
          value={rating}
          onChange={(e) => setRating(parseInt(e.target.value))}
        >
          {[1, 2, 3, 4, 5].map((num) => (
            <option key={num} value={num}>
              {num}
            </option>
          ))}
        </select>
      </div>

      <div>
        <button type="submit">
          {initialReview ? "Update Review" : "Submit Review"}
        </button>
        {initialReview && (
          <button onClick={handleDelete}>Delete Review</button>
        )}

        <Link to="/GameList">
        <button className="cancel-button">Cancel</button>
        </Link>
      </div>
    </form>
  );
};

export default ReviewForm;
