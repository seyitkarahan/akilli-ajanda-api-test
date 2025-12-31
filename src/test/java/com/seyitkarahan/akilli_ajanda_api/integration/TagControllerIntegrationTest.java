package com.seyitkarahan.akilli_ajanda_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seyitkarahan.akilli_ajanda_api.dto.request.AuthRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.request.LoginRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.request.TagRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TagControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Long userId;

    @BeforeEach
    void setup() throws Exception {
        // Register a user
        AuthRequest register = new AuthRequest();
        register.setEmail("tag@test.com");
        register.setPassword("123456");
        register.setName("Tag User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        // Login
        LoginRequest login = new LoginRequest();
        login.setEmail("tag@test.com");
        login.setPassword("123456");

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthResponse authResponse = objectMapper.readValue(response, AuthResponse.class);
        token = authResponse.getToken();
        userId = authResponse.getId();
    }

    @Test
    void createTag_shouldReturnCreatedTag() throws Exception {
        TagRequest request = TagRequest.builder()
                .name("Work")
                .color("#FF0000")
                .build();

        mockMvc.perform(post("/api/tags")
                        .param("userId", String.valueOf(userId))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Work"))
                .andExpect(jsonPath("$.color").value("#FF0000"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void getAllTags_shouldReturnList() throws Exception {
        // Create a tag first
        TagRequest request = TagRequest.builder()
                .name("Personal")
                .color("#00FF00")
                .build();

        mockMvc.perform(post("/api/tags")
                        .param("userId", String.valueOf(userId))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Get all tags
        mockMvc.perform(get("/api/tags")
                        .param("userId", String.valueOf(userId))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name", hasItem("Personal")));
    }

    @Test
    void updateTag_shouldUpdateNameAndColor() throws Exception {
        // Create
        TagRequest create = TagRequest.builder()
                .name("Old Name")
                .color("#000000")
                .build();

        String response = mockMvc.perform(post("/api/tags")
                        .param("userId", String.valueOf(userId))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tagId = objectMapper.readTree(response).get("id").asLong();

        // Update
        TagRequest update = TagRequest.builder()
                .name("New Name")
                .color("#FFFFFF")
                .build();

        mockMvc.perform(put("/api/tags/{id}", tagId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.color").value("#FFFFFF"));
    }

    @Test
    void deleteTag_shouldReturnNoContent() throws Exception {
        // Create
        TagRequest request = TagRequest.builder()
                .name("To Delete")
                .color("#123456")
                .build();

        String response = mockMvc.perform(post("/api/tags")
                        .param("userId", String.valueOf(userId))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tagId = objectMapper.readTree(response).get("id").asLong();

        // Delete
        mockMvc.perform(delete("/api/tags/{id}", tagId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
