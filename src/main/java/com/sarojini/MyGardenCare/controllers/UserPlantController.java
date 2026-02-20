package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserPlantResponse;
import com.sarojini.MyGardenCare.dtos.UserPlantUpdateRequest;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.entities.User;
import com.sarojini.MyGardenCare.services.UserPlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users/{username}/plants")
@RequiredArgsConstructor
public class UserPlantController {
    private final UserPlantService userPlantService;

    @GetMapping
    public ResponseEntity<List<UserPlantResponse>> getAllUserPlants(@AuthenticationPrincipal User user){
        List<UserPlantResponse> userPlants = userPlantService.getAllUserPlants(user);
        return ResponseEntity.ok(userPlants);
    }

    @GetMapping("/by-plant-name")
    public ResponseEntity<List<UserPlantResponse>> getAllUserPlantsByPlantName(@AuthenticationPrincipal User user,
                                                                               @RequestParam("plant-name") String plantName){
       List<UserPlantResponse> userPlantsByPlantName = userPlantService.getAllUserPlantsByPlantName(user, plantName);
       return ResponseEntity.ok(userPlantsByPlantName);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPlantResponse> getUserPlantById(@PathVariable Long id, @AuthenticationPrincipal User user){
        UserPlantResponse userPlantById = userPlantService.getUserPlantById(id, user);
        return ResponseEntity.ok(userPlantById);
    }

    @PostMapping
    public ResponseEntity<UserPlantResponse> createUserPlant(@AuthenticationPrincipal User user,
                                                             @Valid @RequestBody UserPlantCreateRequest createReq){
        UserPlantResponse newUserPlant = userPlantService.createUserPlant(user, createReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserPlant);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserPlantResponse> updateUserPlantById(@PathVariable Long id,
                                                                 @AuthenticationPrincipal User user,
                                                                 @RequestBody UserPlantUpdateRequest updateReq){
        UserPlantResponse updatedUserPlant = userPlantService.updateUserPlantById(user, id, updateReq);
        return ResponseEntity.ok(updatedUserPlant);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserPlantById(@AuthenticationPrincipal User user, @PathVariable Long id){
        userPlantService.deleteUserPlantById(user, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllUserPlants(@AuthenticationPrincipal User user){
        userPlantService.deleteAllUserPlants(user);
        return ResponseEntity.noContent().build();
    }
}
