package com.sarojini.MyGardenCare.dtos;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserCreateRequestDto {
    @NotBlank(message = "Username is required")
    @Schema(example = "Anna")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Schema(example = "anna@gmail.com")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Schema(example = "123")
    private String password;

    @Pattern(regexp = "\\d{5}", message = "zipcode should contain 5 digits")
    @Schema(example = "02345")
    private String zipcode;
}
