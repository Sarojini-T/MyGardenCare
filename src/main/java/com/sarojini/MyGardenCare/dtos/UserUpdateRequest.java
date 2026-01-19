package com.sarojini.MyGardenCare.dtos;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    private String password;

    @Pattern(regexp = "\\d{5}", message = "Zipcode should contain 5 digits")
    private String zipcode;
}
