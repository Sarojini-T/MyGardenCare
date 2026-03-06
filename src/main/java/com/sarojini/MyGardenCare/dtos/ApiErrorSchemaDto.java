package com.sarojini.MyGardenCare.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorSchemaDto {
    private String message;
    private Map<String, String> errors;

    public ApiErrorSchemaDto(String message){
        this.message = message;
    }
}
