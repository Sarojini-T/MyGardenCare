package com.sarojini.MyGardenCare.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;

    @Email(message = "Please provide a valid email")
    private String email;

    private String password;

    @Pattern(regexp = "\\d{5}", message = "Zipcode should contain 5 digits")
    private String zipcode;
}
