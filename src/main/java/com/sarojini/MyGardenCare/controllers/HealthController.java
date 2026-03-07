package com.sarojini.MyGardenCare.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Health")
@RestController
public class HealthController {
    @GetMapping("/health")
    @Operation(
            summary = "Health check",
            description = "Returns OK if the API is running",
            security = {}
    )
    @ApiResponse(
            responseCode = "200",
            description = "API is healthy",
            content = @Content(schema = @Schema(example = "OK")))
    public ResponseEntity<String> healthCheck(){
        return ResponseEntity.ok("OK");
    }
}
