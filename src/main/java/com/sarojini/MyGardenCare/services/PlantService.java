package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.dtos.PlantApiDto;
import com.sarojini.MyGardenCare.dtos.PlantResponse;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlantService {
    private final PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository){
        this.plantRepository = plantRepository;
    }

    public PlantResponse getPlantById(Long id){
        Optional<Plant> plantByIdOptional = plantRepository.findById(id);
        if(plantByIdOptional.isEmpty()) throw new EntityNotFoundException("Plant " + id + " not found");
        return mapPlantToPlantResponse(plantByIdOptional.get());
    }

    public List<PlantResponse> searchPlantByAnyName(String query){
        List<Plant> plants = plantRepository.searchByAnyName(query);

        if(plants.isEmpty()) throw new EntityNotFoundException(query + " not found");

        List<PlantResponse> plantResponseList = new ArrayList<>();

        for(Plant plant : plants){
            plantResponseList.add(mapPlantToPlantResponse(plant));
        }

        return plantResponseList;
    }

    public PlantResponse addPlant(PlantApiDto plantApiDto){
        if(plantRepository.existsByScientificNameIgnoreCase(plantApiDto.getScientificName())){
            throw new ConflictException(plantApiDto.getScientificName() + " already exists");
        }

        Plant newPlant = mapPlantApiDtoToPlant(plantApiDto);
        Plant savedPlant = plantRepository.save(newPlant);
        return mapPlantToPlantResponse(savedPlant);
    }

    public PlantResponse mapPlantToPlantResponse(Plant plant){
        PlantResponse newPlantResponse =  new PlantResponse();

        newPlantResponse.setId(plant.getId());
        newPlantResponse.setCommonName(plant.getCommonName());
        newPlantResponse.setScientificName(plant.getScientificName());

        if(StringUtils.hasText(plant.getAlternateNames())) newPlantResponse.setAlternateNames(plant.getAlternateNames());;
        if(StringUtils.hasText(plant.getLightRequirement())) newPlantResponse.setLightRequirement(plant.getLightRequirement());
        if(StringUtils.hasText(plant.getSoilType())) newPlantResponse.setSoilType(plant.getSoilType());
        if(StringUtils.hasText(plant.getLifeCycle())) newPlantResponse.setLifeCycle(plant.getLifeCycle());
        if(StringUtils.hasText(plant.getWaterRequirement())) newPlantResponse.setWaterRequirement(plant.getWaterRequirement());
        if(plant.getHeightInMeters() != null) newPlantResponse.setHeightInMeters(plant.getHeightInMeters());
        if(plant.getWidthInMeters() != null) newPlantResponse.setWidthInMeters(plant.getWidthInMeters());
        if(StringUtils.hasText(plant.getGrowth())) newPlantResponse.setGrowth(plant.getGrowth());

        return newPlantResponse;
    }

    public Plant mapPlantApiDtoToPlant(PlantApiDto plantApiDto){
        String commonName = plantApiDto.getCommonName();
        String scientificName = plantApiDto.getScientificName();

        Plant newPlant = new Plant(commonName, scientificName);
        newPlant.setAlternateNames(plantApiDto.getAlternateNames());
        newPlant.setLightRequirement(plantApiDto.getLightRequirement());
        newPlant.setSoilType(plantApiDto.getSoilType());
        newPlant.setLifeCycle(plantApiDto.getLifeCycle());
        newPlant.setWaterRequirement(plantApiDto.getWaterRequirement());
        newPlant.setHeightInMeters(plantApiDto.getHeightInMeters());
        newPlant.setWidthInMeters(plantApiDto.getWidthInMeters());
        newPlant.setGrowth(plantApiDto.getGrowth());

        return newPlant;
    }

}
