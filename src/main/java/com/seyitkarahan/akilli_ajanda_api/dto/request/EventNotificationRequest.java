package com.seyitkarahan.akilli_ajanda_api.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventNotificationRequest {

    private LocalDateTime notifyAt;
    private boolean isSent = false;
    private Long eventId;
}
