package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.PlantApiDto;
import com.sarojini.MyGardenCare.dtos.PlantResponse;
import com.sarojini.MyGardenCare.services.PlantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plants")
@Validated
public class PlantController {
    private final PlantService plantService;

    public PlantController(PlantService plantService){
        this.plantService = plantService;
    }

    @GetMapping("{id}")
    public ResponseEntity<PlantResponse> getPlantById(@PathVariable Long id){
        PlantResponse plantById = plantService.getPlantById(id);
        return ResponseEntity.ok(plantById);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlantResponse>> searchByAnyName(@RequestParam(name = "query", required = true)
    @NotBlank(message = "Search query cannot be empty")
    @Size(min = 3, message = "Search query must be at least 2 characters")String query) {
        List<PlantResponse> plantResponseList = plantService.searchPlantByAnyName(query);

        if(plantResponseList.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(plantResponseList);
    }

    @PostMapping
    public ResponseEntity<PlantResponse> addPlant(@Valid @RequestBody PlantApiDto plantApiDto){
        PlantResponse addedPlant = plantService.addPlant(plantApiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedPlant);
    }
}

