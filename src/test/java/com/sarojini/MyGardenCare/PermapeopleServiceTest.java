package com.sarojini.MyGardenCare;

import com.sarojini.MyGardenCare.dtos.PlantApiDto;
import com.sarojini.MyGardenCare.services.externalAPI.PermapeopleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermapeopleServiceTest {
    @Mock private RestClient restClient;
    @Mock private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private RestClient.RequestBodySpec requestBodySpec;
    @Mock private RestClient.ResponseSpec responseSpec;

    @InjectMocks private PermapeopleService permapeopleService;

    private void setUpFluentMock() {
        ReflectionTestUtils.setField(permapeopleService, "keyId", "fake-key");
        ReflectionTestUtils.setField(permapeopleService, "keySecret", "fake-secret");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any(MediaType.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(anyMap())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void searchExternalPlantApi_Success(){
        setUpFluentMock();

        PermapeopleService.PermapeoplePlant mockApiData = new PermapeopleService.PermapeoplePlant();
        mockApiData.setId("1");
        mockApiData.setScientificName("Allium aflatunense");

        PermapeopleService.PermapeopleData alternateName = new PermapeopleService.PermapeopleData();
        alternateName.setKey("Alternate name");
        alternateName.setValue("Ornamental onion, Persian onion");

        PermapeopleService.PermapeopleData height = new PermapeopleService.PermapeopleData();
        height.setKey("Height");
        height.setValue("1.0");

        PermapeopleService.PermapeopleData soilType = new PermapeopleService.PermapeopleData();
        soilType.setKey("Soil type");
        soilType.setValue("Light (sandy), Medium");

        mockApiData.setData(List.of(alternateName, height, soilType));

        PermapeopleService.PermapeopleResponse response = new PermapeopleService.PermapeopleResponse();
        response.setPlants(List.of(mockApiData));

        when(responseSpec.body(PermapeopleService.PermapeopleResponse.class))
                .thenReturn(response);

        List<PlantApiDto> plantApiDto = permapeopleService.searchExternalPlantApi("Ornamental onion");

        assertEquals(1, plantApiDto.size());
        assertEquals("Allium aflatunense", plantApiDto.get(0).getScientificName());
        assertEquals("Ornamental onion", plantApiDto.get(0).getCommonName());
    }

    @Test
    void searchExternalPlantApi_ReturnsEmptyList_WhenNoPlantsFound(){
        setUpFluentMock();

        PermapeopleService.PermapeopleResponse emptyResponse = new PermapeopleService.PermapeopleResponse();

        when(responseSpec.body(PermapeopleService.PermapeopleResponse.class))
                .thenReturn(emptyResponse);

        List<PlantApiDto> result = permapeopleService.searchExternalPlantApi("nonexistentPlant");

        assertTrue(result.isEmpty());
    }

    @Test
    void searchExternalPlantApi_ReturnsEmptyList_WhenApiThrowsException(){
        setUpFluentMock();

        when(responseSpec.body(PermapeopleService.PermapeopleResponse.class))
                .thenThrow(new RuntimeException("Permapeople API is down"));

        List<PlantApiDto> result = permapeopleService.searchExternalPlantApi("Tomato");

        assertTrue(result.isEmpty());
    }


    @Test
    void searchExternalPlantApi_HandlesNullDataArray(){
        PermapeopleService.PermapeoplePlant mockApiData = new PermapeopleService.PermapeoplePlant();
        mockApiData.setId("1");
        mockApiData.setScientificName("Allium aflatunense");
        mockApiData.setData(null);

        PlantApiDto plantApiDto = permapeopleService.mapToPlantApiDto(mockApiData);

        assertEquals("Allium aflatunense", plantApiDto.getScientificName());
        assertNull(plantApiDto.getAlternateNames());
    }

    @Test
    void testMapToPlantApiDto_SafelyHandlesBadHeightData(){
        PermapeopleService.PermapeoplePlant mockApiData = new PermapeopleService.PermapeoplePlant();

        PermapeopleService.PermapeopleData badHeight = new PermapeopleService.PermapeopleData();
        badHeight.setKey("Height");
        badHeight.setValue("1.0-2.0 meters");

        mockApiData.setData(List.of(badHeight));

        PlantApiDto plantApiDto = permapeopleService.mapToPlantApiDto(mockApiData);

        assertEquals(1.0, plantApiDto.getHeightInMeters());

    }

    @Test
    void testMapToPlantApiDto_SafelyHandlesNullHeightData(){
        PermapeopleService.PermapeoplePlant mockApiData = new PermapeopleService.PermapeoplePlant();

        PermapeopleService.PermapeopleData badHeight = new PermapeopleService.PermapeopleData();
        badHeight.setKey("Height");
        badHeight.setValue("");

        mockApiData.setData(List.of(badHeight));

        PlantApiDto plantApiDto = permapeopleService.mapToPlantApiDto(mockApiData);

        assertNull(plantApiDto.getHeightInMeters());
    }

    @Test
    void testMapToPlantApiDto_CatchNumberFormatException_WhenHeightContainsNonNumericCharacters(){
        PermapeopleService.PermapeoplePlant mockApiData = new PermapeopleService.PermapeoplePlant();

        PermapeopleService.PermapeopleData badHeight = new PermapeopleService.PermapeopleData();
        badHeight.setKey("Height");
        badHeight.setValue("one");

        mockApiData.setData(List.of(badHeight));

        PlantApiDto plantApiDto = permapeopleService.mapToPlantApiDto(mockApiData);

        assertNull(plantApiDto.getHeightInMeters());
    }

}
