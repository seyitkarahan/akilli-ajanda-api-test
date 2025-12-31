package com.seyitkarahan.akilli_ajanda_api.dto.response;

import com.seyitkarahan.akilli_ajanda_api.enums.ImportanceLevel;
import com.seyitkarahan.akilli_ajanda_api.enums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ImportanceLevel importanceLevel;
    private Long userId;
    private Long categoryId;
    private Long recurringRuleId;
}
