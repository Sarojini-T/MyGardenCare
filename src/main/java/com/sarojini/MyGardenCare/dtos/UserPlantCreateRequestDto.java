package com.sarojini.MyGardenCare.dtos;
import com.sarojini.MyGardenCare.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPlantCreateRequestDto {
    @NotNull(message = "Plant id cannot be null")
    @Schema(example = "1")
    private Long plantId;

    @NotBlank(message = "Nickname cannot be blank")
    @Schema(example = "Tomato1")
    private String nickname;

    @NotNull(message = "Plant container cannot be null")
    @Schema(example = "POT")
    private PlantContainer plantContainer;

    @NotNull(message = "Plant location cannot be null")
    @Schema(example = "OUTDOOR")
    private PlantLocation plantLocation;

    @Schema(example = "SMALL")
    private ContainerSize containerSize;

    @Schema(example = "true")
    private Boolean hasDrainage;
}
