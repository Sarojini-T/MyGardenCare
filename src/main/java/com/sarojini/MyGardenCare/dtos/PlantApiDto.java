package com.sarojini.MyGardenCare.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlantApiDto {
    @NotBlank(message = "Common name cannot be blank")
    private String commonName;

    @NotBlank(message = "Scientific name cannot be blank")
    private String scientificName;

    private String alternateNames;
    private String lightRequirement;
    private String soilType;
    private String lifeCycle;
    private String waterRequirement;
    private Double heightInMeters;
    private Double widthInMeters;
    private String growth;
}
