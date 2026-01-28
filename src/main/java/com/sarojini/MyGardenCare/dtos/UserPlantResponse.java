package com.sarojini.MyGardenCare.dtos;
import com.sarojini.MyGardenCare.enums.*;
import lombok.Data;

@Data
public class UserPlantResponse {
    private Long userPlantId;
    private Long plantId;
    private String plantName;
    private String nickname;
    private PlantContainer plantContainer;
    private ContainerSize containerSize;
    private Boolean hasDrainage;
    private PlantLocation plantLocation;
}
