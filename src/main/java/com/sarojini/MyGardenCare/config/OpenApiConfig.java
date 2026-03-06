package com.sarojini.MyGardenCare.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MyGardenCare API",
                version = "1.0",
                description = """
                REST API for managing plants, fetching external botanical data, and generating plant care recommendations.
                
                Plant data provided by Permapeople.org (https://permapeople.org)
                
                This project integrates the Permapeople API for plant search functionality only.
                Plant data is licensed under the Creative Commons Attribution-ShareAlike 4.0
                International License (CC BY-SA 4.0).
                               
                
                Features:
                - JWT Authentication
                - External plant search
                - Secure RESTful endpoints
                - User garden management
                """,
                contact = @Contact(
                        email = "sarojini.torchon@gmail.com",
                        url = "https://github.com/Sarojini-T"
                )
        )
)
@SecurityScheme(
    name = "bearerAuth",
        description = "Paste Token to access secure endpoints",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
