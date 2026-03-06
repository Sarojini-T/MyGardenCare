package com.sarojini.MyGardenCare.integration;

import com.sarojini.MyGardenCare.dtos.UserPlantCreateRequest;
import com.sarojini.MyGardenCare.entities.Plant;
import com.sarojini.MyGardenCare.repositories.PlantRepository;
import com.sarojini.MyGardenCare.enums.PlantContainer;
import com.sarojini.MyGardenCare.enums.PlantLocation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserPlantIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private PlantRepository plantRepository;

    @Test
    void authenticatedUserShouldCreateUserPlant() throws Exception {
        Plant masterPlant = new Plant("Tomato", "Solanum lycopersicum");
        plantRepository.save(masterPlant);

        String token = registerUserAndGetToken();

        UserPlantCreateRequest createReq = new UserPlantCreateRequest();
        createReq.setPlantId(1L);
        createReq.setNickname("Tomato");
        createReq.setPlantContainer(PlantContainer.POT);
        createReq.setPlantLocation(PlantLocation.OUTDOOR);

        mockMvc.perform(post("/api/v1/user-plants")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("Tomato"));
    }
}
