package com.sarojini.MyGardenCare.dto;

import com.sarojini.MyGardenCare.entities.UserPlant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPlantUpdateRequest {
    @NotBlank
    private String username;

    @NotNull
    private Long userPlantId;

    private UserPlant.PlantContainer plantContainer;
    private UserPlant.PotSize potSize;
    private Boolean hasDrainage;
    private String soilType;
    private UserPlant.PlantLocation plantLocation;
}
