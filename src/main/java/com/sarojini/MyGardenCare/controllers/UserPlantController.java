package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.ApiErrorSchemaDto;
import com.sarojini.MyGardenCare.dtos.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserPlantResponse;
import com.sarojini.MyGardenCare.dtos.UserPlantUpdateRequest;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.services.UserPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;

@Tag(name = "User's Garden",
        description = "Endpoints for user to get, add and update the plants in their garden")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/user-plants")
@RequiredArgsConstructor
public class UserPlantController {
    private final UserPlantService userPlantService;

    @GetMapping
    @Operation(summary = "Get all plants in this user's garden",
    description = "Get a list of all the plants the current user has. If they have none, an empty list will be returned.",
    responses = {
            @ApiResponse(responseCode = "200", description = "User's plants found and returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserPlantResponse.class)))),
            @ApiResponse(responseCode = "403", description = "Unauthorized request, missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class)))
    })
    public ResponseEntity<List<UserPlantResponse>> getAllUserPlants(@AuthenticationPrincipal User user){
        List<UserPlantResponse> userPlants = userPlantService.getAllUserPlants(user);
        return ResponseEntity.ok(userPlants);
    }

    @GetMapping("/by-plant-name")
    @Operation(summary = "Get plants by name",
    description = """
    Get the plants of the current user by name. A user can have multiple of the same plants in their garden,
    but they differ by characteristics and each have a unique nickname.
    """,
    responses = {
            @ApiResponse(responseCode =  "200", description = "Plants found and returned",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserPlantResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Plants not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized request, missing or invalid JWT",
                    content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class)))
    })
    public ResponseEntity<List<UserPlantResponse>> getAllUserPlantsByPlantName(@AuthenticationPrincipal User user,
                                                                               @Parameter(description = "Common name of the plant", example = "tomato")
                                                                               @RequestParam("plant-name") String plantName){
       List<UserPlantResponse> userPlantsByPlantName = userPlantService.getAllUserPlantsByPlantName(user, plantName);
       return ResponseEntity.ok(userPlantsByPlantName);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get a specific plant by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Plant found", content = @Content(schema = @Schema(implementation = UserPlantResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Plant not found",
                            content = @Content(
                                    schema = @Schema(implementation = ApiErrorSchemaDto.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "message": "User plant 1 not found",
                          "errors": null
                        }
                        """
                                    )
                            )),
                    @ApiResponse(responseCode = "400", description = "Bad request",  content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),
                    @ApiResponse(responseCode = "403", description = "Unauthorized request, missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class)))
            })
    public ResponseEntity<UserPlantResponse> getUserPlantById(@PathVariable Long id, @AuthenticationPrincipal User user){
        UserPlantResponse userPlantById = userPlantService.getUserPlantById(id, user);
        return ResponseEntity.ok(userPlantById);
    }

    @PostMapping
    @Operation(summary = "Add a new plant to this user's garden",
            description = """
                    Link a plant from the plants DB (By id) to a specific user (by id),
                    allowing the user to add specific information about that plant.
                    Users can have two of the same plant, as long as they differ by nickname
                    """,
            responses = {
                    @ApiResponse(responseCode = "201", description = "New plant added to user's garden", content = @Content(schema = @Schema(implementation = UserPlantResponse.class))),
                    @ApiResponse(responseCode = "409", description = "User already has this plant", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),
                    @ApiResponse(responseCode = "400", description = "Plant request was not well formed",
                            content = @Content(
                                    schema = @Schema(implementation = ApiErrorSchemaDto.class),
                                    examples = @ExampleObject(
                                            value = """
                        {
                          "message": "Validation failed",
                          "errors": {
                            "nickname": "Nickname cannot be blank"
                          }
                        }
                        """
                                    )
                            )),
                    @ApiResponse(responseCode = "403", description = "Unauthorized request, missing or invalid JWT",content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class)))
            })
    public ResponseEntity<UserPlantResponse> createUserPlant(@AuthenticationPrincipal User user,
                                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New plant to add to user's garden", required = true)
                                                             @Valid @RequestBody UserPlantCreateRequest createReq){
        UserPlantResponse newUserPlant = userPlantService.createUserPlant(user, createReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserPlant);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a plant by id",
    description = """
           Update the fields of a user's plant, without violating the API's logic:
           If the plantContainer is OUTDOOR_GROUND, plantLocation cannot be INDOOR and containerSize and hasDrainage must be null
           """,
    responses = {
            @ApiResponse(responseCode = "200", description = "Plant updated successfully"),
        @ApiResponse(responseCode = "400", description = "Logic rule violated",
                content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
                examples = {@ExampleObject(
                        value = """
                                message = "plantLocation cannot be INDOOR for OUTDOOR_GROUND",
                                errors : null
                                """
                )})),
        @ApiResponse(responseCode = "409", description = "User already has this plant", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized request, missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class)))
    })
    public ResponseEntity<UserPlantResponse> updateUserPlantById(@PathVariable Long id,
                                                                 @AuthenticationPrincipal User user,
                                                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Partial updates to an existing plant", required = true)
                                                                 @RequestBody UserPlantUpdateRequest updateReq){
        UserPlantResponse updatedUserPlant = userPlantService.updateUserPlantById(user, id, updateReq);
        return ResponseEntity.ok(updatedUserPlant);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a plant by id",
    responses = {
            @ApiResponse(responseCode = "204", description = "Plant deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Plant not found", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized request, missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class)))
    })
    public ResponseEntity<Void> deleteUserPlantById(@AuthenticationPrincipal User user, @PathVariable Long id){
        userPlantService.deleteUserPlantById(user, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete all the plants of this user",
            responses = {
                    @ApiResponse(responseCode = "204", description = "All plants for this user deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized request, missing or invalid JWT", content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class)))
            })
    public ResponseEntity<Void> deleteAllUserPlants(@AuthenticationPrincipal User user){
        userPlantService.deleteAllUserPlants(user);
        return ResponseEntity.noContent().build();
    }
}
