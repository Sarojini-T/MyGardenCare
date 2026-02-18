package com.sarojini.MyGardenCare.services.externalAPI;

import com.sarojini.MyGardenCare.dtos.PlantApiDto;

import java.util.List;

public interface ExternalPlantApiService {
    List<PlantApiDto> searchExternalPlantApi(String query);
}
