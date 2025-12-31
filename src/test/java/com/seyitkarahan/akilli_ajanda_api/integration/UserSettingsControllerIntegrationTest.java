package com.seyitkarahan.akilli_ajanda_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserSettingsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        String email = "usersettings_test_" + System.currentTimeMillis() + "@test.com";

        // REGISTER
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "UserSettings User",
                                  "email": "%s",
                                  "password": "123456"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk());

        // LOGIN
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "123456"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        token = objectMapper.readTree(loginResponse).get("token").asText();
    }

    @Test
    void fullUserSettingsFlow_shouldWork() throws Exception {

        // CREATE / UPDATE SETTINGS
        mockMvc.perform(put("/api/user-settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "theme": "DARK",
                                  "emailNotificationsEnabled": true,
                                  "pushNotificationsEnabled": true,
                                  "defaultTaskReminderMinutes": 30,
                                  "defaultEventReminderMinutes": 60
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("DARK"))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(true))
                .andExpect(jsonPath("$.pushNotificationsEnabled").value(true))
                .andExpect(jsonPath("$.defaultTaskReminderMinutes").value(30))
                .andExpect(jsonPath("$.defaultEventReminderMinutes").value(60));

        // GET SETTINGS
        mockMvc.perform(get("/api/user-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("DARK"))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(true))
                .andExpect(jsonPath("$.pushNotificationsEnabled").value(true));

        // UPDATE SETTINGS
        mockMvc.perform(put("/api/user-settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "theme": "LIGHT",
                                  "emailNotificationsEnabled": false,
                                  "pushNotificationsEnabled": false,
                                  "defaultTaskReminderMinutes": 15,
                                  "defaultEventReminderMinutes": 30
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.theme").value("LIGHT"))
                .andExpect(jsonPath("$.emailNotificationsEnabled").value(false))
                .andExpect(jsonPath("$.pushNotificationsEnabled").value(false))
                .andExpect(jsonPath("$.defaultTaskReminderMinutes").value(15));

        // DELETE SETTINGS
        mockMvc.perform(delete("/api/user-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // VERIFY DELETION
        mockMvc.perform(get("/api/user-settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
