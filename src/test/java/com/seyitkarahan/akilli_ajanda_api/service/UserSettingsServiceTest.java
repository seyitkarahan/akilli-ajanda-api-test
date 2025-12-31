package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.UserSettingsRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.UserSettingsResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.entity.UserSettings;
import com.seyitkarahan.akilli_ajanda_api.exception.UserAlreadyExistsException;
import com.seyitkarahan.akilli_ajanda_api.exception.UserNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.UserSettingsNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSettingsServiceTest {

    @InjectMocks
    private UserSettingsService userSettingsService;

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User user;
    private UserSettings settings;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        settings = UserSettings.builder()
                .id(10L)
                .user(user)
                .theme("dark")
                .language("tr")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    // ---------------- GET SETTINGS ----------------

    @Test
    void getUserSettings_shouldReturnSettings() {
        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.of(settings));

        UserSettingsResponse response = userSettingsService.getUserSettings();

        assertEquals(settings.getId(), response.getId());
        assertEquals("dark", response.getTheme());
    }

    @Test
    void getUserSettings_whenNotFound_shouldThrowException() {
        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(UserSettingsNotFoundException.class,
                () -> userSettingsService.getUserSettings());
    }

    // ---------------- CREATE SETTINGS ----------------

    @Test
    void createSettings_shouldCreateSuccessfully() {
        UserSettingsRequest request = UserSettingsRequest.builder()
                .theme("light")
                .language("en")
                .build();

        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.empty());
        when(userSettingsRepository.save(any(UserSettings.class))).thenReturn(settings);

        UserSettingsResponse response = userSettingsService.createSettings(request);

        assertNotNull(response);
        verify(userSettingsRepository).save(any(UserSettings.class));
    }

    @Test
    void createSettings_whenAlreadyExists_shouldThrowException() {
        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.of(settings));

        UserSettingsRequest request = UserSettingsRequest.builder().build();

        assertThrows(UserAlreadyExistsException.class,
                () -> userSettingsService.createSettings(request));
    }

    // ---------------- UPDATE SETTINGS ----------------

    @Test
    void updateSettings_shouldUpdateOnlyProvidedFields() {
        UserSettingsRequest request = UserSettingsRequest.builder()
                .theme("light")
                .is24HourFormat(true)
                .build();

        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.of(settings));
        when(userSettingsRepository.save(settings)).thenReturn(settings);

        UserSettingsResponse response = userSettingsService.updateSettings(request);

        assertEquals(settings.getId(), response.getId());
        verify(userSettingsRepository).save(settings);
    }

    @Test
    void updateSettings_whenNotFound_shouldThrowException() {
        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.empty());

        UserSettingsRequest request = UserSettingsRequest.builder().build();

        assertThrows(UserNotFoundException.class,
                () -> userSettingsService.updateSettings(request));
    }

    // ---------------- DELETE SETTINGS ----------------

    @Test
    void deleteSettings_shouldDeleteSuccessfully() {
        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.of(settings));

        userSettingsService.deleteSettings();

        verify(userSettingsRepository).delete(settings);
    }

    @Test
    void deleteSettings_whenNotFound_shouldThrowException() {
        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(UserSettingsNotFoundException.class,
                () -> userSettingsService.deleteSettings());
    }
}

