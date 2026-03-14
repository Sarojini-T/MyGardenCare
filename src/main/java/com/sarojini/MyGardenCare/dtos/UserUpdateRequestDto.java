package com.sarojini.MyGardenCare.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequestDto {
    @Schema(example = "Anna02")
    private String username;

    @Email(message = "Please provide a valid email")
    @Schema(example = "")
    private String email;

    @Schema(example = "abc")
    private String password;

    @Pattern(regexp = "\\d{5}", message = "Zipcode should contain 5 digits")
    @Schema(example = "02222")
    private String zipcode;
}
