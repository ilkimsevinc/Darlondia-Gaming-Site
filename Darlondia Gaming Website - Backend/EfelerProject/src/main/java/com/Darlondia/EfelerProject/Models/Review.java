package com.Darlondia.EfelerProject.Models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.Darlondia.EfelerProject.Utilities.ObjectIdSerializer;
import java.time.LocalDateTime;

@Document(collection = "reviews")
public class Review {

    @Id
    private String id;

    @Field("user_id")
    private String userId;


    @Field("content")
    private String content;

    @Field("rating")
    private int rating;

    @Field("created_at")
    private LocalDateTime createdAt;
    @Field("game_id")
    private String gameId;
    @Field("username")
    private String username;
    @Field("game_name")
    private String gameName;

    // Getters and setters for each field

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getContent() {
        return content;
    }
    public String getUsername() {
        return username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

}
