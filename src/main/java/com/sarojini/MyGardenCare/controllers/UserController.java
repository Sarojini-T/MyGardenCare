package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.UserResponse;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Principal principal){
        return ResponseEntity.ok(userService.getUserByUsername(principal.getName()));
    }

   @PatchMapping("/me")
   public ResponseEntity<UserResponse> updateMyProfile(Principal principal,
                                                      @Valid @RequestBody UserUpdateRequest userUpdateReq){
        UserResponse updatedUserById =  userService.updateMyProfile(principal.getName(), userUpdateReq);
        return ResponseEntity.ok(updatedUserById);
   }

   @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile(Principal principal){
        userService.deleteByUsername(principal.getName());
        return ResponseEntity.noContent().build();
   }

}
