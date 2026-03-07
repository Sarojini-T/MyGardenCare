package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.ApiErrorSchemaDto;
import com.sarojini.MyGardenCare.dtos.PlantApiDto;
import com.sarojini.MyGardenCare.dtos.PlantResponse;
import com.sarojini.MyGardenCare.services.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

@Tag(name = "Plants", description = "Endpoints to lookup plants by their id or plant name and to add new plants to the plants DB")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/plants")
@RequiredArgsConstructor
@Validated
public class PlantController {
    private final PlantService plantService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a plant from the DB by id",
    responses = {
            @ApiResponse(responseCode = "200",
                    description = "Plant found",
            content = @Content(schema = @Schema(implementation = PlantResponse.class))),

            @ApiResponse(responseCode = "404"
                    , description = "Plant does not exist in DB",
            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
            examples = {
                    @ExampleObject(
                            value = """
                                    {
                                    message: Plant 10 not found in DB,
                                    errors: null
                                    }
                                    """
                    )
            }))
    })
    public ResponseEntity<PlantResponse> getPlantById(@PathVariable Long id){
        PlantResponse plantById = plantService.getPlantById(id);
        return ResponseEntity.ok(plantById);
    }

    @GetMapping
    @Operation(summary = "Lookup plant by name",
    description = """
            Type in at least 2 characters to lookup a plant, either by its common name or
            its scientific name
            """,
    responses = {
            @ApiResponse(responseCode = "200",
                    description = "Plant found",
                    content = @Content(schema = @Schema(implementation = PlantResponse.class))),

            @ApiResponse(responseCode = "400",
                    description = "Bad plant request",
                    content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                    message: Validation failed,
                                                    errors: { query: Search query must be at least 2 characters }
                                                    }
                                                    """
                                    )
                            })),

            @ApiResponse(responseCode = "404",
                    description = "Plant not found in database or Permapeople API",
                    content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                                            {
                                            message: Plant grapefruit not found in database or external API,
                                            errors: null
                                            }
                                            """
                            )
                    }))
    })
    public ResponseEntity<PlantResponse> getPlantByName(
            @NotBlank(message = "Search query cannot be empty")
            @Size(min = 2, message = "Search query must be at least 2 characters")
            @Parameter(description = "Plant name to lookup", example = "tomato")
            @RequestParam(name = "query", required = true)
            String query) {
        PlantResponse plantResponse = plantService.getPlantByName(query);
        return ResponseEntity.ok(plantResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Admin-only: add plant from Permapeople API to DB",
    description = "When a user looks up a new plant, the plant service will add it to the DB",
    responses = {
            @ApiResponse(responseCode = "201",
                    description = "New plant added to DB",
            content = @Content(schema = @Schema(implementation = PlantResponse.class))),

            @ApiResponse(responseCode = "400", description = "API returned invalid data",
            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
            examples = {@ExampleObject(
                    value = """ 
                            { message : Validation failed,
                            errors: { scientificName : Scientific name cannot be blank }
                            }
                    """
            )})),

            @ApiResponse(responseCode = "403", description = "Must be an admin to access this endpoint", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),

            @ApiResponse(responseCode = "409", description = "Plant already exists in DB",
            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
            examples = {@ExampleObject(value = """
                    {
                    message : Solanum lycopersicum already exists,
                    errors : null
                    }""")}))

    })
    public ResponseEntity<PlantResponse> addPlant(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Permapeople API data mapped to plants DB schema", required = true)
            @Valid @RequestBody PlantApiDto plantApiDto){
        PlantResponse addedPlant = plantService.addPlant(plantApiDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedPlant);
    }
}

