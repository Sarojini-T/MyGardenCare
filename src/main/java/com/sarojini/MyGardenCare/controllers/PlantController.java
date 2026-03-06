package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.PlantApiDto;
import com.sarojini.MyGardenCare.dtos.PlantResponse;
import com.sarojini.MyGardenCare.services.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@Tag(name = "Plants")
@RestController
@RequestMapping("/api/v1/plants")
@Validated
public class PlantController {
    private final PlantService plantService;

    public PlantController(PlantService plantService){
        this.plantService = plantService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResponse> getPlantById(@PathVariable Long id){
        PlantResponse plantById = plantService.getPlantById(id);
        return ResponseEntity.ok(plantById);
    }

    @Operation(summary = "Get a plant by its common name or scientific name",
    description = "Search for plants using the Permapeople API. Query must have at least 2 characters to be valid.",
    responses = {
            @ApiResponse(responseCode = "200", description = "Plant retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Plant not found in DB or external API"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping
    public ResponseEntity<PlantResponse> getPlantByName(
            @Parameter(description = "Plant name to search", example = "tomato")
            @RequestParam(name = "query", required = true)
    @NotBlank(message = "Search query cannot be empty")
    @Size(min = 3, message = "Search query must be at least 2 characters")String query) {
        PlantResponse plantResponse = plantService.getPlantByName(query);

        return ResponseEntity.ok(plantResponse);
    }

    @PostMapping
    public ResponseEntity<PlantResponse> addPlant(@Valid @RequestBody PlantApiDto plantApiDto){
        PlantResponse addedPlant = plantService.addPlant(plantApiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedPlant);
    }
}

