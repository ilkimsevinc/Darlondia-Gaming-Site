package com.Darlondia.EfelerProject.Repositories;

import com.Darlondia.EfelerProject.Models.GameList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GameListRepository extends MongoRepository<GameList, String> {
    List<GameList> findByUserId(String userId);
}
