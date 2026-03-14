package com.sarojini.MyGardenCare.controllers;
import com.sarojini.MyGardenCare.dtos.ApiErrorSchemaDto;
import com.sarojini.MyGardenCare.dtos.UserResponseDto;
import com.sarojini.MyGardenCare.dtos.UserUpdateRequestDto;
import com.sarojini.MyGardenCare.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

@Tag(name = "User", description = "Endpoints to get information about the current user or all users")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Admin-only: get a list of all users",
    responses = {
            @ApiResponse(responseCode = "200", description = "All users returned",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))),

            @ApiResponse(responseCode = "403", description = "Must be an admin to see all users",
            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
            examples = {@ExampleObject(value = """
    { message : Access denied,
    errors : null }""")}))

    })
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Admin-only: get a user by their id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found and returned successfully",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),

                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
                            examples = {@ExampleObject(value = """ 
                    { message : User 100 not found,
                    errors : null }""")})),

                    @ApiResponse(responseCode = "403", description = "Must be an admin to see user",
                            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
                                    examples = {@ExampleObject(value = """
            { message : Access denied,
            errors : null }""")}))
            })
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Admin-only: delete a user by their id",
    responses = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),

            @ApiResponse(responseCode = "403", description = "Must be an admin to delete a user by id",
                    content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
                            examples = {@ExampleObject(value = """
            { message : Access denied,
            errors : null }""")})),

            @ApiResponse(responseCode = "404", description = "User to delete not found",
            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
            examples = {
                    @ExampleObject(value = """
                                { User 100 not found,
                                errors : null }""")
            }))
    })
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's profile information",
    responses = {
            @ApiResponse(responseCode = "200", description = "Current user's information found", content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
    })
    public ResponseEntity<UserResponseDto> getMyProfile(Principal principal){
        return ResponseEntity.ok(userService.getUserByUsername(principal.getName()));
    }

    @PatchMapping("/me")
    @Operation(summary = "Update the current user's profile information",
    responses = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),

            @ApiResponse(responseCode = "409", description = "One updated field makes this user a duplicate",
            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
            examples = {@ExampleObject(value = """ 
    {
    message : Username already taken,
    errors: null
    }""")}))
    })
   public ResponseEntity<UserResponseDto> updateMyProfile(Principal principal,
                                                          @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to update for current user", required = true)
                                                      @Valid @RequestBody UserUpdateRequestDto userUpdateReq){
        UserResponseDto updatedUserById =  userService.updateMyProfile(principal.getName(), userUpdateReq);
        return ResponseEntity.ok(updatedUserById);
   }

   @DeleteMapping("/me")
   @Operation(summary = "Delete current user's profile",
   responses = {
           @ApiResponse(responseCode = "204", description = "User deleted successfully")
   })
    public ResponseEntity<Void> deleteMyProfile(Principal principal){
        userService.deleteByUsername(principal.getName());
        return ResponseEntity.noContent().build();
   }

}
