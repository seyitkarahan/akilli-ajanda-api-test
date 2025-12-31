package com.seyitkarahan.akilli_ajanda_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seyitkarahan.akilli_ajanda_api.dto.request.EventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long categoryId;

    @BeforeEach
    void setup() throws Exception {
        // register
        int status = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Event Test",
                                  "email": "event@test.com",
                                  "password": "123456"
                                }
                                """))
                .andReturn()
                .getResponse()
                .getStatus();
        assertThat(status == 200 || status == 409).isTrue();

        // login
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "event@test.com",
                                  "password": "123456"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        token = objectMapper.readTree(loginResponse).get("token").asText();

        // category oluştur
        String categoryResponse = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Event Category"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        categoryId = objectMapper.readTree(categoryResponse).get("id").asLong();
    }

    @Test
    void createEvent_shouldReturnCreatedEvent() throws Exception {
        EventRequest request = EventRequest.builder()
                .title("Meeting")
                .description("Project meeting")
                .categoryId(categoryId)
                .build();

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Meeting"))
                .andExpect(jsonPath("$.categoryId").value(categoryId));
    }

    @Test
    void getAllEvents_shouldReturnEventList() throws Exception {
        // event oluştur
        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Call",
                                  "description": "Client call",
                                  "categoryId": %d
                                }
                                """.formatted(categoryId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItem("Call")));
    }

    @Test
    void getEventById_shouldReturnEvent() throws Exception {
        String response = mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Workshop",
                                  "description": "Spring Boot",
                                  "categoryId": %d
                                }
                                """.formatted(categoryId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long eventId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/events/{id}", eventId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Workshop"));
    }

    @Test
    void deleteEvent_shouldReturnNoContent() throws Exception {
        String response = mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Delete Me",
                                  "description": "Temp",
                                  "categoryId": %d
                                }
                                """.formatted(categoryId)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long eventId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/events/{id}", eventId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
