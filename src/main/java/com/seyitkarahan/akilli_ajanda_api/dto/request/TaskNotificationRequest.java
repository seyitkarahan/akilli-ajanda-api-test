package com.seyitkarahan.akilli_ajanda_api.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskNotificationRequest {

    private LocalDateTime notifyAt;
    private boolean isSent = false;
    private Long taskId;
}
