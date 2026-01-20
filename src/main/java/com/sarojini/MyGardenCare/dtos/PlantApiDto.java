package com.sarojini.MyGardenCare.dtos;

import lombok.Data;

@Data
public class PlantApiDto {
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
