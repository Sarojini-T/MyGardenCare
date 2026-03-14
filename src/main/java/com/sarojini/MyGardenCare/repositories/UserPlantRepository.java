package com.sarojini.MyGardenCare.repositories;
import com.sarojini.MyGardenCare.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sarojini.MyGardenCare.entities.UserPlant;

import java.util.List;
import java.util.Optional;

public interface UserPlantRepository extends JpaRepository<UserPlant, Long> {
    List<UserPlant> findByUser(User user);
    Optional<UserPlant> findByIdAndUser(Long id, User user);
    List<UserPlant> findByUserAndPlantId(User user, Long id);

    Boolean existsByUserAndNicknameIgnoreCase(User user, String nickname);

    void deleteByUser(User user);
    void deleteByUserAndId(User user, Long id);
}