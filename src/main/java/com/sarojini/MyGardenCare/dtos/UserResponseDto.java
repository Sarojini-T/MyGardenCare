package com.sarojini.MyGardenCare.dtos;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {
    @Schema(example = "1")
    private Long id;

    @Schema(example = "Anna")
    private String username;

    @Schema(example = "anna@gmail.com")
    private String email;

    @Schema(example = "01234")
    private String zipcode;
}
