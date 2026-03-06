package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.UserResponse;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequest;
import com.sarojini.MyGardenCare.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@Tag(name = "User")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserByUsername(@RequestParam("username") String username){
        UserResponse userByUsername = userService.getUserByUsername(username);
        return ResponseEntity.ok(userByUsername);
    }

    @Operation(summary = "Update user by id",
    responses = {
            @ApiResponse(responseCode =  "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "One updated field makes this user a duplicate"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
   @PatchMapping("/{id}")
   public ResponseEntity<UserResponse> updateUserById(@PathVariable Long id,
                                                      @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                              description = "User fields to update",
                                                              required = true
                                                      )
                                                      @Valid @RequestBody UserUpdateRequest userUpdateReq){
        UserResponse updatedUserById =  userService.updateUserById(id, userUpdateReq);
        return ResponseEntity.ok(updatedUserById);
   }

    @Operation(summary = "Delete user by id",
            responses = {
                    @ApiResponse(responseCode =  "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized request")
            })
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteById(@PathVariable Long id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
   }

}
