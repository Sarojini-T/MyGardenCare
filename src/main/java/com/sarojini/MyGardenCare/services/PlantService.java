package com.sarojini.MyGardenCare.services;

import com.sarojini.MyGardenCare.services.externalAPI.ExternalPlantApiService;
import com.sarojini.MyGardenCare.dtos.PlantApiDto;
import com.sarojini.MyGardenCare.dtos.PlantResponseDto;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;
    private final ExternalPlantApiService externalPlantApiService;

    public PlantResponseDto getPlantById(Long id){
        Optional<Plant> plantByIdOptional = plantRepository.findById(id);
        if(plantByIdOptional.isEmpty()) throw new EntityNotFoundException("Plant " + id + " not found");
        return mapPlantToPlantResponse(plantByIdOptional.get());
    }

    public PlantResponseDto getPlantByName(String query){
        List<Plant> localPlants = plantRepository.searchByAnyName(query);

        if(!localPlants.isEmpty()){
            return  mapPlantToPlantResponse((localPlants.get(0)));

        }

        List<PlantApiDto> externalApiPlants = externalPlantApiService.searchExternalPlantApi(query);

        if(externalApiPlants.isEmpty()){
            throw new EntityNotFoundException("Plant " + query + " not found in database or external API");
        }

        PlantApiDto validPlantFromApi = null;

        for(PlantApiDto currPlant : externalApiPlants){
            if(StringUtils.hasText(currPlant.getScientificName()) &&  StringUtils.hasText(currPlant.getCommonName())){
                validPlantFromApi  = currPlant;
                break;
            }
        }

        if(validPlantFromApi == null){
            log.warn("Api returned invalid results for {}", query);
            throw new EntityNotFoundException(("API returned incomplete data for " + query + "."));
        }

        Optional<Plant> plantFromDb = plantRepository.findByScientificNameIgnoreCase(validPlantFromApi.getScientificName());
        if(plantFromDb.isPresent()){
            return mapPlantToPlantResponse(plantFromDb.get());
        }else{
            Plant newPlant = mapPlantApiDtoToPlant(validPlantFromApi);
            Plant savedPlant = plantRepository.save(newPlant);
            return mapPlantToPlantResponse(savedPlant);
        }
    }

    public PlantResponseDto addPlant(PlantApiDto plantApiDto){
        if(plantRepository.existsByScientificNameIgnoreCase(plantApiDto.getScientificName())){
            throw new ConflictException(plantApiDto.getScientificName() + " already exists");
        }

        Plant newPlant = mapPlantApiDtoToPlant(plantApiDto);
        Plant savedPlant = plantRepository.save(newPlant);
        return mapPlantToPlantResponse(savedPlant);
    }

    public static PlantResponseDto mapPlantToPlantResponse(Plant plant){
        PlantResponseDto newPlantResponseDto =  new PlantResponseDto();

        newPlantResponseDto.setId(plant.getId());
        newPlantResponseDto.setCommonName(plant.getCommonName());
        newPlantResponseDto.setScientificName(plant.getScientificName());

        if(StringUtils.hasText(plant.getAlternateNames())) newPlantResponseDto.setAlternateNames(plant.getAlternateNames());;
        if(StringUtils.hasText(plant.getLightRequirement())) newPlantResponseDto.setLightRequirement(plant.getLightRequirement());
        if(StringUtils.hasText(plant.getSoilType())) newPlantResponseDto.setSoilType(plant.getSoilType());
        if(StringUtils.hasText(plant.getLifeCycle())) newPlantResponseDto.setLifeCycle(plant.getLifeCycle());
        if(StringUtils.hasText(plant.getWaterRequirement())) newPlantResponseDto.setWaterRequirement(plant.getWaterRequirement());
        if(plant.getHeightInMeters() != null) newPlantResponseDto.setHeightInMeters(plant.getHeightInMeters());
        if(plant.getWidthInMeters() != null) newPlantResponseDto.setWidthInMeters(plant.getWidthInMeters());
        if(StringUtils.hasText(plant.getGrowth())) newPlantResponseDto.setGrowth(plant.getGrowth());

        return newPlantResponseDto;
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
