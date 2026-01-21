package com.sarojini.MyGardenCare.dtos;
import com.sarojini.MyGardenCare.enums.*;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPlantUpdateRequest {
    @NotBlank
    private String username;
    private PlantContainer plantContainer;
    private PotSize potSize;
    private Boolean hasDrainage;
    private String soilType;
    private PlantLocation plantLocation;
}
