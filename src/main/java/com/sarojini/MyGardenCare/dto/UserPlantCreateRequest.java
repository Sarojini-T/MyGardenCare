package com.sarojini.MyGardenCare.dto;
import com.sarojini.MyGardenCare.entities.UserPlant;
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
    private UserPlant.PlantContainer plantContainer;

    @NotNull
    private UserPlant.PlantLocation plantLocation;

    private UserPlant.PotSize potSize;
    private Boolean hasDrainage;
    private String soilType;



}
