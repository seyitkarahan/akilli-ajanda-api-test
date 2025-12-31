package com.seyitkarahan.akilli_ajanda_api.dto.request;

import com.seyitkarahan.akilli_ajanda_api.enums.ImportanceLevel;
import com.seyitkarahan.akilli_ajanda_api.enums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ImportanceLevel importanceLevel;
    private Long categoryId;
    private Long recurringRuleId;
}
