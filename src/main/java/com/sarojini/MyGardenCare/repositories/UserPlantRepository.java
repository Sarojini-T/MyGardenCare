package com.sarojini.MyGardenCare.repositories;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sarojini.MyGardenCare.entities.UserPlant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sarojini.MyGardenCare.enums.*;

import java.util.List;
import java.util.Optional;

public interface UserPlantRepository extends JpaRepository<UserPlant, Long> {
    List<UserPlant> findByUser(User user);
    Optional<UserPlant> findByIdAndUser(Long id, User user);
    List<UserPlant> findByUserAndPlantIn(User user, List<Plant> plantList);

    void deleteByUser(User user);
    void deleteByUserAndPlantIn(User user, List<Plant> plant);
}
