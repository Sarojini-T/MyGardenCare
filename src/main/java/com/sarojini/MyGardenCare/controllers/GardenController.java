package com.sarojini.MyGardenCare.controllers;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class GardenController {
    @GetMapping("/health")
    public String healthCheck(){
        return "OK";
    }
}
