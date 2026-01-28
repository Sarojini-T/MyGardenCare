package com.sarojini.MyGardenCare.dtos;
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

    private ContainerSize containerSize;
    private Boolean hasDrainage;
}
