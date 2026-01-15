package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dto.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.dto.UserPlantResponse;
import com.sarojini.MyGardenCare.dto.UserPlantUpdateRequest;
import com.sarojini.MyGardenCare.services.UserPlantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/user-plants")
public class UserPlantController {
    private final UserPlantService userPlantService;

    public UserPlantController(UserPlantService userPlantService){
        this.userPlantService = userPlantService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserPlantResponse>> getAllUserPlants(@RequestParam("username") String username){
        List<UserPlantResponse> userPlants = userPlantService.getAllUserPlants(username);
        if(userPlants.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userPlants);
    }

    @GetMapping("/query")
    public ResponseEntity<List<UserPlantResponse>> getAllUserPlantsByPlantName(@RequestParam("username") String username,
                                                                       @RequestParam("plant-name") String plantName){
       List<UserPlantResponse> userPlantsByPlantName = userPlantService.getAllUserPlantsByPlantName(username, plantName);
       if(userPlantsByPlantName.isEmpty()) return ResponseEntity.notFound().build();
       return ResponseEntity.ok(userPlantsByPlantName);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserPlantResponse> getUserPlantById(@PathVariable Long userPlantId,
                                                      @RequestParam("username") String username){
        UserPlantResponse userPlantById = userPlantService.getUserPlantById(userPlantId, username);
        return ResponseEntity.ok(userPlantById);
    }

    @PostMapping("/add")
    public ResponseEntity<UserPlantResponse> createUserPlant(@RequestBody UserPlantCreateRequest createReq){
        UserPlantResponse newUserPlant = userPlantService.createUserPlant(createReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserPlant);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserPlantResponse> updateUserPlantById(@PathVariable Long id,
                                                                 @RequestBody UserPlantUpdateRequest updateReq){
        UserPlantResponse updatedUserPlant = userPlantService.updateUserPlantById(id, updateReq);
        return ResponseEntity.ok(updatedUserPlant);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserPlantById(@PathVariable("id") Long id){
        userPlantService.deleteUserPlantById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-plant-name")
    public ResponseEntity<Void> deleteUserPlantsByName(@RequestParam("username") String username,
                                                       @RequestParam("plant-name") String plantName){
        userPlantService.deleteUserPlantsByName(username, plantName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAllUserPlants(@RequestParam("username")String username){
        userPlantService.deleteAllUserPlants(username);
        return ResponseEntity.noContent().build();
    }
}
