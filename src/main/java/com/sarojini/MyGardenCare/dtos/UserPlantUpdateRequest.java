package com.sarojini.MyGardenCare.dtos;

import com.sarojini.MyGardenCare.enums.*;

import lombok.Data;

@Data
public class UserPlantUpdateRequest {
    private String nickname;
    private String plantName;
    private PlantContainer plantContainer;
    private PlantLocation plantLocation;
    private ContainerSize containerSize;
    private Boolean hasDrainage;
}
