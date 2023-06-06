package com.Darlondia.EfelerProject.Repositories;

import com.Darlondia.EfelerProject.Models.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GameRepository extends MongoRepository<Game, String> {
    List<Game> findByName(String name);
}