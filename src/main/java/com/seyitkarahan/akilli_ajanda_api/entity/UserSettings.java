package com.seyitkarahan.akilli_ajanda_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String theme; // LIGHT, DARK, SYSTEM
    private String language; // tr, en

    private String startDayOfWeek; // MONDAY, SUNDAY
    private String dateFormat; // dd/MM/yyyy, MM/dd/yyyy
    private Boolean is24HourFormat; // true for 24h, false for 12h

    private Boolean emailNotificationsEnabled;
    private Boolean pushNotificationsEnabled;
    
    private Integer defaultTaskReminderMinutes;
    private Integer defaultEventReminderMinutes;

    private String timezone;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}