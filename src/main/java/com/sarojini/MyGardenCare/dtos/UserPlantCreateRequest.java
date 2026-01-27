package com.MyGardenCare.dtos;
import com.MyGardenCare.enums.PlantContainer;
import com.MyGardenCare.enums.PlantLocation;
import com.MyGardenCare.enums.PotSize;
import com.sarojini.MyGardenCare.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPlantCreateRequest {
    @NotNull(message = "Plant id cannot be null")
    private Long plantId;

    @NotBlank(message = "Nickname cannot be blank")
    private String nickname;

    @NotNull(message = "Plant container cannot be null")
    private PlantContainer plantContainer;

    @NotNull(message = "Plant location cannot be null")
    private PlantLocation plantLocation;

    private PotSize potSize;
    private Boolean hasDrainage;
    private String soilType;
}
