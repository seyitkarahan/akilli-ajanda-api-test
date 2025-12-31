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
class TaskNotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long taskId;
    private Long notificationId;

    @BeforeEach
    void setup() throws Exception {
        String email = "tasknotif_test_" + System.currentTimeMillis() + "@test.com";

        // REGISTER
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "TaskNotification User",
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

        // CREATE A TASK (needed for notifications)
        String createTaskResponse = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Task for Notification",
                                  "description": "Task description",
                                  "categoryId": null
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        taskId = objectMapper.readTree(createTaskResponse).get("id").asLong();
    }

    @Test
    void fullTaskNotificationFlow_shouldWork() throws Exception {

        // CREATE NOTIFICATION
        String createResponse = mockMvc.perform(post("/api/notifications/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taskId": %d,
                                  "notifyAt": "2025-12-30T10:00:00",
                                  "sent": false
                                }
                                """.formatted(taskId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        notificationId = objectMapper.readTree(createResponse).get("id").asLong();

        // GET ALL NOTIFICATIONS
        mockMvc.perform(get("/api/notifications/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        // GET BY ID
        mockMvc.perform(get("/api/notifications/tasks/{id}", notificationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notificationId))
                .andExpect(jsonPath("$.taskId").value(taskId));

        // UPDATE NOTIFICATION
        mockMvc.perform(put("/api/notifications/tasks/{id}", notificationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taskId": %d,
                                  "notifyAt": "2025-12-31T15:00:00",
                                  "sent": true
                                }
                                """.formatted(taskId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sent").value(true))
                .andExpect(jsonPath("$.notifyAt").value("2025-12-31T15:00:00"));

        // DELETE NOTIFICATION
        mockMvc.perform(delete("/api/notifications/tasks/{id}", notificationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
