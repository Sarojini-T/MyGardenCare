package com.sarojini.MyGardenCare.controllers;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "health")
@RestController
public class HealthController {
    @GetMapping("/health")
    public String healthCheck(){
        return "OK";
    }
}
