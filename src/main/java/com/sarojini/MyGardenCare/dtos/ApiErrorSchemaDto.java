package com.sarojini.MyGardenCare.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Schema(description = "Standard API error response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorSchemaDto {
    @Schema(description = "Error message describing what went wrong")
    private String message;

    @Schema(description = "Field-specific validation errors")
    private Map<String, String> errors;

    public ApiErrorSchemaDto(String message){
        this.message = message;
    }
}
