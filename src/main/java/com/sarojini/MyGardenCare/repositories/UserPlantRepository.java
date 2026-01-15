package com.sarojini.MyGardenCare.repositories;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sarojini.MyGardenCare.entities.UserPlant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserPlantRepository extends JpaRepository<UserPlant, Long> {
    List<UserPlant> findByUser(User user);
    Optional<UserPlant> findByIdAndUser(Long id, User user);
    List<UserPlant> findByUserAndPlantIn(User user, List<Plant> plantList);

    @Query("""
    SELECT CASE WHEN COUNT(up) > 0 THEN TRUE ELSE FALSE END
    FROM UserPlant up
    WHERE up.user = :user
      AND up.plant = :plant
      AND up.plantContainer = :plantContainer
      AND up.plantLocation = :plantLocation
      AND (:potSize IS NULL OR up.potSize = :potSize)
      AND (:hasDrainage IS NULL OR up.hasDrainage = :hasDrainage)
      AND (:soilType IS NULL OR up.soilType = :soilType)
    """)
    boolean isDuplicate(
            @Param("user") User user,
            @Param("plant") Plant plant,
            @Param("plantContainer") UserPlant.PlantContainer plantContainer,
            @Param("plantLocation") UserPlant.PlantLocation plantLocation,
            @Param("potSize") UserPlant.PotSize potSize,
            @Param("hasDrainage") Boolean hasDrainage,
            @Param("soilType") String soilType
    );

    void deleteByUser(User user);
    void deleteByUserAndPlantIn(User user, List<Plant> plant);
}
