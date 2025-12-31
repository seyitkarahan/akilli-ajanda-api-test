package com.seyitkarahan.akilli_ajanda_api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventNotificationResponse {

    private Long id;
    private LocalDateTime notifyAt;
    private boolean isSent;
    private Long eventId;
}
