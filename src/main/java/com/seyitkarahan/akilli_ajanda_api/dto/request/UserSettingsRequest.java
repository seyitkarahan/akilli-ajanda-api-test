package com.seyitkarahan.akilli_ajanda_api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettingsRequest {
    private String theme;
    private String language;
    private String startDayOfWeek;
    private String dateFormat;
    private Boolean is24HourFormat;
    private Boolean emailNotificationsEnabled;
    private Boolean pushNotificationsEnabled;
    private Integer defaultTaskReminderMinutes;
    private Integer defaultEventReminderMinutes;
    private String timezone;
}