package com.Darlondia.EfelerProject.Services;

import com.Darlondia.EfelerProject.Models.GameList;
import com.Darlondia.EfelerProject.Repositories.GameListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameListService {
    private final GameListRepository gameListRepository;

    @Autowired
    public GameListService(GameListRepository gameListRepository) {
        this.gameListRepository = gameListRepository;
    }
    public List<GameList> getAllLists() {
        return gameListRepository.findAll();
    }
    // Add methods for creating, updating, and deleting lists, and adding/removing games from lists
}
