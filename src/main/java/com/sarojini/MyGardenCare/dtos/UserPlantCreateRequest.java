package com.sarojini.MyGardenCare.dtos;
import com.sarojini.MyGardenCare.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPlantCreateRequest {
    @NotBlank
    private String username;

    @NotNull
    private Long plantId;

    @NotNull
    private PlantContainer plantContainer;

    @NotNull
    private PlantLocation plantLocation;

    private PotSize potSize;
    private Boolean hasDrainage;
    private String soilType;
}
