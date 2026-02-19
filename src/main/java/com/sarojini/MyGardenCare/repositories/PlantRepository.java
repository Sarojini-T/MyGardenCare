package com.sarojini.MyGardenCare.repositories;
import com.sarojini.MyGardenCare.entities.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    @Query("""
    SELECT p FROM Plant p 
    WHERE LOWER(p.commonName) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(p.alternateNames) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Plant> searchByAnyName(@Param("query") String query);
    Optional<Plant> findByCommonName(String commonName);
    Optional<Plant> findByScientificNameIgnoreCase(String scientificName);
    Boolean existsByScientificNameIgnoreCase(String scientificName);
}

