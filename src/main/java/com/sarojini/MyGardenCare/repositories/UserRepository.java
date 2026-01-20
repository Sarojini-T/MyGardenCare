package com.sarojini.MyGardenCare.repositories;
import org.springframework.data.repository.CrudRepository;
import com.sarojini.MyGardenCare.entities.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
}


