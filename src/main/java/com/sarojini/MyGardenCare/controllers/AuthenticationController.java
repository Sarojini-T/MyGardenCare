package com.sarojini.MyGardenCare.controllers;

import com.sarojini.MyGardenCare.dtos.ApiErrorSchemaDto;
import com.sarojini.MyGardenCare.dtos.UserCreateRequestDto;
import com.sarojini.MyGardenCare.dtos.AuthenticationRequestDto;
import com.sarojini.MyGardenCare.dtos.AuthenticationResponseDto;
import com.sarojini.MyGardenCare.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for user registration and authentication using JWTs")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register a new user",
    description = "Registers new user and generates a JWT token",
    responses = {
            @ApiResponse(responseCode = "201",
                    description = "New user registered successfully and JWT token generated",
            content = @Content(schema = @Schema(implementation = AuthenticationResponseDto.class))),

            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                            { message : Validation failed,
                            errors: { email : Please provide a valid email address }
                            }"""
                            )
                    }))
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New user information", required = true)
            @Valid @RequestBody UserCreateRequestDto createReq){
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(createReq));
    }

    @Operation(summary = "Authenticate a user",
    description = "Validates credentials and returns a JWT token for authenticated API access",
    responses = {
            @ApiResponse(responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = AuthenticationResponseDto.class))
            ),

            @ApiResponse(responseCode = "401",
                    description = "Unauthorized request, missing or invalid JWT",
            content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class))),

            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorSchemaDto.class),
                    examples = {
                            @ExampleObject(
                                    value = """
                            { message: Authentication failed: invalid username or password,
                            errors: null
                            }"""
                            )
                    }))
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User login information", required = true)
            @RequestBody AuthenticationRequestDto authReq){
        return ResponseEntity.ok(authenticationService.authenticate((authReq)));
    }
}
