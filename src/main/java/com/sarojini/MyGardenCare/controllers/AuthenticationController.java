package com.sarojini.MyGardenCare.controllers;

import com.sarojini.MyGardenCare.dtos.UserCreateRequest;
import com.sarojini.MyGardenCare.dtos.AuthenticationRequest;
import com.sarojini.MyGardenCare.dtos.AuthenticationResponse;
import com.sarojini.MyGardenCare.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication",
        description = "Endpoints for user registration and authentication using JWTs")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user",
    description = "Registers new user and generates a JWT token",
    responses = {
            @ApiResponse(responseCode = "201", description = "New user registered successfully and JWT token generated"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New user information",
                    required = true
            )
            @RequestBody UserCreateRequest createReq){
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(createReq));
    }

    @Operation(summary = "Authenticate a user",
    description = "Validates credentials and returns a JWT token for authenticated API access",
    responses = {
            @ApiResponse(responseCode = "200",
                    description = "User authenticated successfully"
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized request, missing or invalid JWT"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login information",
                    required = true
            )
            @RequestBody AuthenticationRequest authReq){
        return ResponseEntity.ok(authenticationService.authenticate((authReq)));
    }
}
