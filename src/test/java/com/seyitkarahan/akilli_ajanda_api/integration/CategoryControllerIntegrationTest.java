package com.seyitkarahan.akilli_ajanda_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seyitkarahan.akilli_ajanda_api.dto.request.AuthRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.request.CategoryRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.request.LoginRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.AuthResponse;
import org.hamcrest.Matchers;
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
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setup() throws Exception {
        AuthRequest register = new AuthRequest();
        register.setEmail("category@test.com");
        register.setPassword("123456");
        register.setName("Test");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest();
        login.setEmail("category@test.com");
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
    }

    @Test
    void createCategory_shouldReturnCreatedCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Work");

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Work"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createCategory_shouldReturnConflict_whenCategoryExists() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Duplicate");

        // create first time
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // create second time -> should fail
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllCategories_shouldReturnList() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Personal");

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].name").value(Matchers.hasItem("Personal")));
    }

    @Test
    void updateCategory_shouldUpdateName() throws Exception {
        CategoryRequest create = new CategoryRequest();
        create.setName("Old Name");

        String response = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = objectMapper.readTree(response).get("id").asLong();

        CategoryRequest update = new CategoryRequest();
        update.setName("New Name");

        mockMvc.perform(put("/api/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void updateCategory_shouldReturnNotFound_whenCategoryDoesNotExist() throws Exception {
        CategoryRequest update = new CategoryRequest();
        update.setName("No Category");

        mockMvc.perform(put("/api/categories/{id}", 9999)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_shouldReturnNoContent() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Temp");

        String response = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/categories/{id}", categoryId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_shouldReturnNotFound_whenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", 9999)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void accessWithoutToken_shouldReturnUnauthorized() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("NoAuth");

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized());
    }
}
