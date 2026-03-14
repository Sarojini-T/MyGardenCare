package com.sarojini.MyGardenCare.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarojini.MyGardenCare.dtos.AuthenticationResponseDto;
import com.sarojini.MyGardenCare.dtos.UserCreateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
        "API_KEY_ID=dummy-permapeople-id",
        "API_KEY_SECRET=dummy-permapeople-secret"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();


    protected String registerUserAndGetToken() throws Exception{
        UserCreateRequestDto createReq = new UserCreateRequestDto();
        createReq.setUsername("user" + System.currentTimeMillis());
        createReq.setEmail("user" + System.currentTimeMillis() + "@gmail.com");
        createReq.setPassword("123");
        createReq.setZipcode("12345");

        String response = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthenticationResponseDto authResp = objectMapper.readValue(response, AuthenticationResponseDto.class);
        return "Bearer " + authResp.getToken();
    }
}
