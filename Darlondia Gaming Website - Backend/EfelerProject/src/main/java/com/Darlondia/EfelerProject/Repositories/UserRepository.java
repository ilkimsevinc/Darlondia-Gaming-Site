package com.Darlondia.EfelerProject.Repositories;

import com.Darlondia.EfelerProject.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
    User findByEmail(String email);
    // Add the following line
    User findByResetCode(String resetCode);
}
