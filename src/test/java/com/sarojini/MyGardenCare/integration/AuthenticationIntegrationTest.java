package com.sarojini.MyGardenCare.integration;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationIntegrationTest extends BaseIntegrationTest{
    @Test
    void registerShouldReturnTokenAndAllowAccessToProtectedEndpoint() throws Exception{
        String token = registerUserAndGetToken();

        mockMvc.perform(get("/api/v1/users/user01/plants")
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/user01/plants"))
                .andExpect(status().isForbidden());
    }
}
