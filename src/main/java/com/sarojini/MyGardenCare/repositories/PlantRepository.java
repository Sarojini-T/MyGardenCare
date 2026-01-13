package com.sarojini.MyGardenCare.repositories;
import com.sarojini.MyGardenCare.entities.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    @Query("""
    SELECT p FROM Plant p 
    WHERE LOWER(p.commonName) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(p.alternateNames) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Plant> searchByAnyName(@Param("query") String query);
}

