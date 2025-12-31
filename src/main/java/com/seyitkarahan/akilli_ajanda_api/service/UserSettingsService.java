package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.UserSettingsRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.UserSettingsResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.entity.UserSettings;
import com.seyitkarahan.akilli_ajanda_api.exception.UserAlreadyExistsException;
import com.seyitkarahan.akilli_ajanda_api.exception.UserNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.UserSettingsNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserSettingsRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;

    public UserSettingsService(UserSettingsRepository userSettingsRepository, UserRepository userRepository) {
        this.userSettingsRepository = userSettingsRepository;
        this.userRepository = userRepository;
    }

    public UserSettingsResponse getUserSettings() {
        User user = getCurrentUser();
        UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseThrow(() -> new UserSettingsNotFoundException("User settings not found"));
        return mapToResponse(settings);
    }

    public UserSettingsResponse createSettings(UserSettingsRequest request) {
        User user = getCurrentUser();

        if (userSettingsRepository.findByUser(user).isPresent()) {
            throw new UserAlreadyExistsException("User settings already exist, use update instead");
        }

        UserSettings settings = UserSettings.builder()
                .user(user)
                .theme(request.getTheme())
                .language(request.getLanguage())
                .startDayOfWeek(request.getStartDayOfWeek())
                .dateFormat(request.getDateFormat())
                .is24HourFormat(request.getIs24HourFormat())
                .emailNotificationsEnabled(request.getEmailNotificationsEnabled())
                .pushNotificationsEnabled(request.getPushNotificationsEnabled())
                .defaultTaskReminderMinutes(request.getDefaultTaskReminderMinutes())
                .defaultEventReminderMinutes(request.getDefaultEventReminderMinutes())
                .timezone(request.getTimezone())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return mapToResponse(userSettingsRepository.save(settings));
    }

    public UserSettingsResponse updateSettings(UserSettingsRequest request) {
        User user = getCurrentUser();
        UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("User settings not found"));

        if (request.getTheme() != null) settings.setTheme(request.getTheme());
        if (request.getLanguage() != null) settings.setLanguage(request.getLanguage());
        if (request.getStartDayOfWeek() != null) settings.setStartDayOfWeek(request.getStartDayOfWeek());
        if (request.getDateFormat() != null) settings.setDateFormat(request.getDateFormat());
        if (request.getIs24HourFormat() != null) settings.setIs24HourFormat(request.getIs24HourFormat());
        if (request.getEmailNotificationsEnabled() != null) settings.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        if (request.getPushNotificationsEnabled() != null) settings.setPushNotificationsEnabled(request.getPushNotificationsEnabled());
        if (request.getDefaultTaskReminderMinutes() != null) settings.setDefaultTaskReminderMinutes(request.getDefaultTaskReminderMinutes());
        if (request.getDefaultEventReminderMinutes() != null) settings.setDefaultEventReminderMinutes(request.getDefaultEventReminderMinutes());
        if (request.getTimezone() != null) settings.setTimezone(request.getTimezone());
        
        settings.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(userSettingsRepository.save(settings));
    }

    public void deleteSettings() {
        User user = getCurrentUser();
        UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseThrow(() -> new UserSettingsNotFoundException("User settings not found"));
        userSettingsRepository.delete(settings);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }

    private UserSettingsResponse mapToResponse(UserSettings settings) {
        return UserSettingsResponse.builder()
                .id(settings.getId())
                .theme(settings.getTheme())
                .language(settings.getLanguage())
                .startDayOfWeek(settings.getStartDayOfWeek())
                .dateFormat(settings.getDateFormat())
                .is24HourFormat(settings.getIs24HourFormat())
                .emailNotificationsEnabled(settings.getEmailNotificationsEnabled())
                .pushNotificationsEnabled(settings.getPushNotificationsEnabled())
                .defaultTaskReminderMinutes(settings.getDefaultTaskReminderMinutes())
                .defaultEventReminderMinutes(settings.getDefaultEventReminderMinutes())
                .timezone(settings.getTimezone())
                .userId(settings.getUser().getId())
                .build();
    }
}