package com.sarojini.MyGardenCare.dtos;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank
    private String username;

    @NotBlank
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank
    private String password;

    @Pattern(regexp = "\\d{5}", message = "zipcode should contain 5 digits")
    private String zipcode;
}
