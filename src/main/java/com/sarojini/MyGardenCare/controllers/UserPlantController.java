package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserPlantResponse;
import com.sarojini.MyGardenCare.dtos.UserPlantUpdateRequest;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.services.UserPlantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users/{username}/plants")
public class UserPlantController {
    private final UserPlantService userPlantService;

    public UserPlantController(UserPlantService userPlantService){
        this.userPlantService = userPlantService;
    }

    @GetMapping
    public ResponseEntity<List<UserPlantResponse>> getAllUserPlants(@PathVariable String username){
        List<UserPlantResponse> userPlants = userPlantService.getAllUserPlants(username);
        return ResponseEntity.ok(userPlants);
    }

    @GetMapping("/by-plant-name")
    public ResponseEntity<List<UserPlantResponse>> getAllUserPlantsByPlantName(@PathVariable String username, @RequestParam("plant-name") String plantName){
       List<UserPlantResponse> userPlantsByPlantName = userPlantService.getAllUserPlantsByPlantName(username, plantName);
       return ResponseEntity.ok(userPlantsByPlantName);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPlantResponse> getUserPlantById(@PathVariable Long id, @PathVariable  String username){
        UserPlantResponse userPlantById = userPlantService.getUserPlantById(id, username);
        return ResponseEntity.ok(userPlantById);
    }

    @PostMapping
    public ResponseEntity<UserPlantResponse> createUserPlant(@PathVariable String username,
                                                             @Valid @RequestBody UserPlantCreateRequest createReq){
        UserPlantResponse newUserPlant = userPlantService.createUserPlant(username, createReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserPlant);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserPlantResponse> updateUserPlantById(@PathVariable Long id,
                                                                 @PathVariable String username,
                                                                 @RequestBody UserPlantUpdateRequest updateReq){
        UserPlantResponse updatedUserPlant = userPlantService.updateUserPlantById(username, id, updateReq);
        return ResponseEntity.ok(updatedUserPlant);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserPlantById(@PathVariable String username, @PathVariable Long id){
        userPlantService.deleteUserPlantById(username, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllUserPlants(@PathVariable String username){
        userPlantService.deleteAllUserPlants(username);
        return ResponseEntity.noContent().build();
    }
}
