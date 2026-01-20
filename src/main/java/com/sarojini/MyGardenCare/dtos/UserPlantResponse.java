package com.sarojini.MyGardenCare.dto;
import com.sarojini.MyGardenCare.enums.*;
import lombok.Data;

@Data
public class UserPlantResponse {
    private Long userPlantId;
    private Long plantId;
    private String plantName;
    private PlantContainer plantContainer;
    private PotSize potSize;
    private Boolean hasDrainage;
    private String soilType;
    private PlantLocation plantLocation;
}
