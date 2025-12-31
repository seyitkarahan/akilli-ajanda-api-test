package com.seyitkarahan.akilli_ajanda_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ImageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long eventId;
    private Long imageId;

    @BeforeEach
    void setup() throws Exception {
        String email = "image_test_" + System.currentTimeMillis() + "@test.com";

        // REGISTER
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Image User",
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

        // EVENT CREATE (image event'e bağlıysa)
        String eventResponse = mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Image Event",
                                  "description": "Event for image test",
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
    void fullImageFlow_shouldWork() throws Exception {

        // FILE
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "dummy-image-content".getBytes()
        );

        // UPLOAD
        String uploadResponse = mockMvc.perform(multipart("/api/images/upload")
                        .file(file)
                        .param("eventId", eventId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        imageId = objectMapper.readTree(uploadResponse).get("id").asLong();

        // GET MY IMAGES
        mockMvc.perform(get("/api/images/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        // GET BY EVENT
        mockMvc.perform(get("/api/images/event/{eventId}", eventId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(imageId));

        // DELETE
        mockMvc.perform(delete("/api/images/{id}", imageId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
