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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventNotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long eventId;

    @BeforeEach
    void setup() throws Exception {
        String email = "event_notification_" + System.currentTimeMillis() + "@test.com";

        // REGISTER
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Event Notification User",
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

        // 👉 EVENT CREATE (ZORUNLU)
        String eventResponse = mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Test Event",
                                  "description": "Integration Test Event",
                                  "startTime": "2025-12-30T09:00:00",
                                  "endTime": "2025-12-30T10:00:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        eventId = objectMapper.readTree(eventResponse).get("id").asLong();
    }

    @Test
    void fullEventNotificationFlow_shouldWork() throws Exception {

        // CREATE
        String createResponse = mockMvc.perform(post("/api/notifications/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": %d,
                                  "notifyAt": "2025-12-30T08:30:00"
                                }
                                """.formatted(eventId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long notificationId = objectMapper.readTree(createResponse).get("id").asLong();

        // GET ALL
        mockMvc.perform(get("/api/notifications/events")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        // GET BY ID
        mockMvc.perform(get("/api/notifications/events/{id}", notificationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notificationId));

        // UPDATE
        mockMvc.perform(put("/api/notifications/events/{id}", notificationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": %d,
                                  "notifyAt": "2025-12-30T07:00:00"
                                }
                                """.formatted(eventId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifyAt").value("2025-12-30T07:00:00"));

        // DELETE
        mockMvc.perform(delete("/api/notifications/events/{id}", notificationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
