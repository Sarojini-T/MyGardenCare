package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.dtos.PlantApiDto;
import com.sarojini.MyGardenCare.dtos.PlantResponseDto;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.exceptions.ConflictException;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import com.sarojini.MyGardenCare.services.PlantService;
import com.sarojini.MyGardenCare.services.externalAPI.ExternalPlantApiService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantServiceTest {
    @Mock
    private PlantRepository plantRepository;

    @Mock
    private ExternalPlantApiService externalPlantApiService;

    @InjectMocks
    private PlantService plantService;

    private Plant mockPlant;
    private PlantApiDto mockPlantApiDto;

    @BeforeEach
    void setUp(){
        mockPlant = new Plant("Tomato", "Solanum lycopersicum");
        ReflectionTestUtils.setField(mockPlant, "id", 1L);

        mockPlantApiDto = new PlantApiDto();
        mockPlantApiDto.setCommonName("Tomato");
        mockPlantApiDto.setScientificName("Solanum lycopersicum");
    }

    @Test
    void getPlantById_Success(){
        when(plantRepository.findById(1L)).thenReturn(Optional.of(mockPlant));

        PlantResponseDto response = plantService.getPlantById(1L);

        assertNotNull(response);
        assertEquals("Tomato", response.getCommonName());
        assertEquals("Solanum lycopersicum", response.getScientificName());
        verify(plantRepository, times(1)).findById(1L);
    }

    @Test
    void getPlantById_ThrowsEntityNotFoundException_WhenPlantNotFound(){
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> plantService.getPlantById(1L));
        verify(plantRepository, times(1)).findById(1L);
    }

    @Test
    void getPlantByName_Success_WhenPlantFoundInDB(){
        when(plantRepository.searchByAnyName("Tomato")).thenReturn(List.of(mockPlant));

        PlantResponseDto response = plantService.getPlantByName("Tomato");

        assertNotNull(response);
        assertEquals("Tomato", response.getCommonName());
        assertEquals("Solanum lycopersicum", response.getScientificName());

        verify(externalPlantApiService, never()).searchExternalPlantApi(anyString());
    }

    @Test
    void getPlantByName_Success_WhenPlantFetchedFromExternalApi(){
        when(plantRepository.searchByAnyName("Tomato")).thenReturn(new ArrayList<>());
        when(externalPlantApiService.searchExternalPlantApi("Tomato")).thenReturn(List.of(mockPlantApiDto));
        when(plantRepository.findByScientificNameIgnoreCase("Solanum lycopersicum")).thenReturn(Optional.empty());
        when(plantRepository.save(any(Plant.class))).thenAnswer(i -> i.getArgument(0));

        PlantResponseDto response = plantService.getPlantByName("Tomato");

        assertNotNull(response);
        assertEquals("Tomato", response.getCommonName());
        assertEquals("Solanum lycopersicum", response.getScientificName());

        verify(externalPlantApiService, times(1)).searchExternalPlantApi("Tomato");
        verify(plantRepository, times(1)).save(any(Plant.class));
    }

    @Test
    void getPlantByName_ThrowsEntityNotFoundException_WhenPlantNotFoundInApiAndDB(){
        when(plantRepository.searchByAnyName("fakePlant")).thenReturn(new ArrayList<>());
        when(externalPlantApiService.searchExternalPlantApi("fakePlant")).thenReturn(new ArrayList<>());

        assertThrows(EntityNotFoundException.class, () -> plantService.getPlantByName("fakePlant"));

        verify(plantRepository, never()).save(any(Plant.class));
    }

    @Test
    void getPlantByName_ThrowsEntityNotFoundException_WhenApiDataIsIncomplete(){
        String query = "Hillside Blueberry";

        when(plantRepository.searchByAnyName(query)).thenReturn(new ArrayList<>());

        PlantApiDto incompleteDto = new PlantApiDto();
        incompleteDto.setScientificName("Vaccinium constablaei");
        incompleteDto.setCommonName("");

        when(externalPlantApiService.searchExternalPlantApi(query)).thenReturn(List.of(incompleteDto));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            plantService.getPlantByName(query);
        });

        assertEquals("API returned incomplete data for Hillside Blueberry.", exception.getMessage());

        verify(plantRepository, never()).findByScientificNameIgnoreCase(anyString());
        verify(plantRepository, never()).save(any(Plant.class));
    }

    @Test
    void addPlant_Success(){
        when(plantRepository.existsByScientificNameIgnoreCase(anyString())).thenReturn(false);
        when(plantRepository.save(any(Plant.class))).thenAnswer(i -> i.getArgument(0));

        PlantResponseDto response = plantService.addPlant(mockPlantApiDto);

        assertNotNull(response);
        assertEquals("Solanum lycopersicum", response.getScientificName());
        verify(plantRepository, times(1)).save(any(Plant.class));
    }

    @Test
    void addPlant_WhenPlantDataHasAllFields(){
        mockPlantApiDto.setAlternateNames("Garden tomato, Love apple");
        mockPlantApiDto.setLifeCycle("Annual, Perennial");
        mockPlantApiDto.setGrowth("Fast");
        mockPlantApiDto.setSoilType("Light (sandy), Medium, Heavy (clay)");
        mockPlantApiDto.setLightRequirement("Full sun");
        mockPlantApiDto.setWaterRequirement("Moist");
        mockPlantApiDto.setHeightInMeters(2.0);
        mockPlantApiDto.setWidthInMeters(1.0);

        when(plantRepository.existsByScientificNameIgnoreCase(anyString())).thenReturn(false);
        when(plantRepository.save(any(Plant.class))).thenAnswer(i -> i.getArgument(0));

        PlantResponseDto response = plantService.addPlant(mockPlantApiDto);

        assertNotNull(response);
        assertEquals("Solanum lycopersicum", response.getScientificName());
        assertEquals("Garden tomato, Love apple", response.getAlternateNames());

        verify(plantRepository, times(1)).save(any(Plant.class));
    }

    @Test
    void addPlant_ThrowConflictException_WhenPlantExistsAlready(){
        when(plantRepository.existsByScientificNameIgnoreCase("Solanum lycopersicum")).thenReturn(true);

        assertThrows(ConflictException.class, () -> plantService.addPlant(mockPlantApiDto));
        verify(plantRepository, never()).save(any(Plant.class));
    }
}
