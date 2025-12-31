package com.seyitkarahan.akilli_ajanda_api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageUploadRequest {
    private Long taskId;
    private Long eventId;
}
