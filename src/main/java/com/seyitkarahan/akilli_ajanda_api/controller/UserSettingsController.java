package com.seyitkarahan.akilli_ajanda_api.controller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.UserSettingsRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.UserSettingsResponse;
import com.seyitkarahan.akilli_ajanda_api.service.UserSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @GetMapping
    public ResponseEntity<UserSettingsResponse> getUserSettings() {
        return ResponseEntity.ok(userSettingsService.getUserSettings());
    }

    @PostMapping
    public ResponseEntity<UserSettingsResponse> createSettings(@RequestBody UserSettingsRequest request) {
        return ResponseEntity.ok(userSettingsService.createSettings(request));
    }

    @PutMapping
    public ResponseEntity<UserSettingsResponse> updateSettings(@RequestBody UserSettingsRequest request) {
        return ResponseEntity.ok(userSettingsService.updateSettings(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSettings() {
        userSettingsService.deleteSettings();
        return ResponseEntity.noContent().build();
    }
}

