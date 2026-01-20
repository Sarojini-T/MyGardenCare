package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.UserCreateRequest;
import com.sarojini.MyGardenCare.dtos.UserResponse;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        UserResponse userById = userService.getUserById(id);
        return ResponseEntity.ok(userById);
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam("username") String username){
        UserResponse userByUsername = userService.getUserByUsername(username);
        return ResponseEntity.ok(userByUsername);
    }

   @PostMapping
    public ResponseEntity<UserResponse> createNewUser(@RequestBody UserCreateRequest userCreateReq){
        UserResponse newUser = userService.createNewUser(userCreateReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
   }

   @PutMapping("/{id}")
   public ResponseEntity<UserResponse> updateUserById(@PathVariable Long id,
                                                      @RequestBody UserUpdateRequest userUpdateReq){
       UserResponse updatedUserById =  userService.updateUserById(id, userUpdateReq);
        return ResponseEntity.ok(updatedUserById);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteById(@PathVariable Long id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
   }

}
