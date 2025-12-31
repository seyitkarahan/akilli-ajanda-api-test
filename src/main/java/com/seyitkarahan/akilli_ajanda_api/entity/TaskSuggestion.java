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
@Table(name = "task_suggestions")
public class TaskSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String suggestedTaskText;

    private String parsedTitle;

    private String parsedCategory;

    private LocalDateTime parsedStartTime;

    private LocalDateTime parsedEndTime;

    private boolean isAccepted = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
