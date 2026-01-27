package com.MyGardenCare.repositories;
import org.springframework.data.repository.CrudRepository;
import com.MyGardenCare.entities.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    Boolean existsByUsernameIgnoreCase(String username);
    Boolean existsByEmailIgnoreCase(String email);
}


