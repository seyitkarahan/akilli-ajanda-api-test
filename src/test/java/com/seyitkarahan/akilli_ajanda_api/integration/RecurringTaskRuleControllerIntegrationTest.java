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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecurringTaskRuleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long ruleId;

    @BeforeEach
    void setup() throws Exception {
        String email = "rule_test_" + System.currentTimeMillis() + "@test.com";

        // REGISTER
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Rule User",
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
    void fullRecurringRuleFlow_shouldWork() throws Exception {

        // CREATE
        String createResponse = mockMvc.perform(post("/api/recurring-rules")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "frequency": "WEEKLY",
                                  "dayOfWeek": "MONDAY",
                                  "time": "09:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ruleId = objectMapper.readTree(createResponse).get("id").asLong();

        // GET ALL
        mockMvc.perform(get("/api/recurring-rules")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        // GET BY ID
        mockMvc.perform(get("/api/recurring-rules/{id}", ruleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ruleId))
                .andExpect(jsonPath("$.frequency").value("WEEKLY"));

        // UPDATE
        mockMvc.perform(put("/api/recurring-rules/{id}", ruleId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "frequency": "DAILY",
                                  "dayOfWeek": "FRIDAY",
                                  "time": "10:30"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.frequency").value("DAILY"))
                .andExpect(jsonPath("$.dayOfWeek").value("FRIDAY"))
                .andExpect(jsonPath("$.time").value("10:30"));

        // DELETE
        mockMvc.perform(delete("/api/recurring-rules/{id}", ruleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
