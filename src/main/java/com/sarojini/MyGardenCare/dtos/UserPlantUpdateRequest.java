package com.MyGardenCare.dtos;
import com.MyGardenCare.enums.PlantContainer;
import com.MyGardenCare.enums.PlantLocation;
import com.MyGardenCare.enums.PotSize;
import com.sarojini.MyGardenCare.enums.*;

import lombok.Data;

@Data
public class UserPlantUpdateRequest {
    private PlantContainer plantContainer;
    private PotSize potSize;
    private Boolean hasDrainage;
    private String soilType;
    private PlantLocation plantLocation;
}
