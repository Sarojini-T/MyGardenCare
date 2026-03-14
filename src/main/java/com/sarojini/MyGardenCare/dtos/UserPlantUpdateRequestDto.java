package com.sarojini.MyGardenCare.dtos;

import com.sarojini.MyGardenCare.enums.*;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserPlantUpdateRequestDto {
    @Schema(example = "tomato2")
    private String nickname;

    @Schema(example = "")
    private String plantName;

    @Schema(example = "")
    private PlantContainer plantContainer;

    @Schema(example = "INDOOR")
    private PlantLocation plantLocation;

    @Schema(example = "")
    private ContainerSize containerSize;

    @Schema(example = "")
    private Boolean hasDrainage;
}
