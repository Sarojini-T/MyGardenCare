package com.sarojini.MyGardenCare.dto;
import com.sarojini.MyGardenCare.entities.UserPlant;
import lombok.Data;

@Data
public class UserPlantRequestDTO {
    private String username;
    private String plantName;
    private UserPlant.PlantContainer plantContainer;
    private UserPlant.PotSize potSize;
    private Boolean hasDrainage;
    private String soilType;
    private UserPlant.PlantLocation plantLocation;

}
