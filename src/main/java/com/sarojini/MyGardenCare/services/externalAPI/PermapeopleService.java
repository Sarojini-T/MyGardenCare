package com.sarojini.MyGardenCare.services.externalAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sarojini.MyGardenCare.dtos.PlantApiDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PermapeopleService implements ExternalPlantApiService{
    private final RestClient restClient;

    @Value("${permapeople.key-id}")
    private String keyId;

    @Value("${permapeople.key-secret}")
    private String keySecret;

    private static final String SEARCH_URL = "https://permapeople.org/api/search";

    @Override
    public List<PlantApiDto> searchExternalPlantApi(String query){
        try{
            PermapeopleResponse response = restClient.post()
                    .uri(SEARCH_URL)
                    .header("x-permapeople-key-id", keyId)
                    .header("x-permapeople-key-secret", keySecret)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("q", query))
                    .retrieve()
                    .body(PermapeopleResponse.class);

            if(response == null || response.getPlants() == null){
                return new ArrayList<>();
            }
            List<PlantApiDto> plantApiDtoList = new ArrayList<>();

            for(PermapeoplePlant permapeoplePlant : response.getPlants()){
                plantApiDtoList.add(mapToPlantApiDto(permapeoplePlant));
            }
            return plantApiDtoList;
        } catch(Exception e){
            System.err.println("Failed to fetch from Permapeople: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static PlantApiDto mapToPlantApiDto(PermapeoplePlant permapeoplePlant){
        PlantApiDto plantApiDto = new PlantApiDto();

        Map<String, String> permapeopleDataMap = new HashMap<>();
        if(permapeoplePlant.getData() != null){
            for(PermapeopleData data : permapeoplePlant.getData()){
                if(data.getKey() != null && data.getValue() != null){
                    permapeopleDataMap.put(data.getKey(), data.getValue());
                }
            }
        }

        String alternateNames = permapeopleDataMap.get("Alternate name");

        if(alternateNames != null){
            String[] names = alternateNames.split(",");
            plantApiDto.setCommonName(names[0].trim());
            plantApiDto.setAlternateNames(alternateNames);
        }

        plantApiDto.setScientificName(permapeoplePlant.getScientificName());
        plantApiDto.setLightRequirement(permapeopleDataMap.get("Light requirement"));
        plantApiDto.setSoilType(permapeopleDataMap.get("Soil type"));
        plantApiDto.setWaterRequirement(permapeopleDataMap.get("Water requirement"));
        plantApiDto.setHeightInMeters(parseDouble(permapeopleDataMap.get("Height")));
        plantApiDto.setWidthInMeters(parseDouble(permapeopleDataMap.get("Width") ));
        plantApiDto.setGrowth(permapeopleDataMap.get("Growth"));

        return plantApiDto;
    }

    public static Double parseDouble(String value){
        if(value == null || value.isBlank()){
            return null;
        }
        try{
            String val = value.split("-")[0].replaceAll("[^0-9.]", "").trim();
            return Double.parseDouble(val);
        }catch(NumberFormatException e){
            return null;
        }
    }

    @Data
    public static class PermapeopleResponse{
        private List<PermapeoplePlant> plants;
    }

    @Data
    public static class PermapeoplePlant{
        private String id;

        @JsonProperty("scientific_name")
        private String scientificName;

        private List<PermapeopleData> data;
    }

    @Data
    public static class PermapeopleData{
        private String key;
        private String value;
    }

}
