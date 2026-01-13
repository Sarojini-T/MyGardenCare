package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.sarojini.MyGardenCare.entities.Plant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plants")
@Validated
public class PlantController {
    private final PlantRepository plantRepository;

    public PlantController(final PlantRepository plantRepository){
        this.plantRepository = plantRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Plant>> searchByAnyName(@RequestParam(name = "query", required = true)
    @NotBlank(message = "Search query cannot be empty")
    @Size(min = 3, message = "Search query must be at least 2 characters")String query) {
        List<Plant> plants = this.plantRepository.searchByAnyName(query);

        if(plants.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(plants);
    }
}

