package com.sarojini.MyGardenCare.dtos;
import com.sarojini.MyGardenCare.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserPlantResponse {
    @Schema(example = "1")
    private Long userPlantId;

    @Schema(description = "Id of a plant stored in the plants DB", example = "1")
    private Long plantId;

    @Schema(example = "Heirloom tomato")
    private String plantName;

    @Schema(example = "tomato1")
    private String nickname;

    @Schema(example = "POT")
    private PlantContainer plantContainer;

    @Schema(example = "SMALL")
    private ContainerSize containerSize;

    @Schema(example = "true")
    private Boolean hasDrainage;

    @Schema(example = "OUTDOOR")
    private PlantLocation plantLocation;

    @Schema(description = "Dynamic care recommendations for this plant")
    private List<String> plantCareRecommendations;
}
