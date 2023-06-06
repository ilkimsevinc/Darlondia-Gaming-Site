package com.Darlondia.EfelerProject.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "game_lists")
public class GameList {
    @Id
    private String id;

    private String userId;
    private String name;
    private List<String> games;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getGames() {
        return games;
    }

    public void setGames(List<String> games) {
        this.games = games;
    }

}
