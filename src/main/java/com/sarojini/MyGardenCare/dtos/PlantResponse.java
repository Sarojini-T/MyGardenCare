package com.sarojini.MyGardenCare.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PlantResponse {
    @Schema(example = "1")
    private Long id;

    @Schema(example = "Heirloom Tomato")
    private String commonName;

    @Schema(example = "Solanum lycopersicum")
    private String scientificName;

    @Schema(example = "Tomato, Red Heirloom")
    private String alternateNames;

    @Schema(example = "Full sun")
    private String lightRequirement;

    @Schema(example = "Loamy")
    private String soilType;

    @Schema(example = "Annual")
    private String lifeCycle;

    @Schema(example = "Medium")
    private String waterRequirement;

    @Schema(example = "1.5")
    private Double heightInMeters;

    @Schema(example = "0.6")
    private Double widthInMeters;

    @Schema(example = "Bushy")
    private String growth;
}
