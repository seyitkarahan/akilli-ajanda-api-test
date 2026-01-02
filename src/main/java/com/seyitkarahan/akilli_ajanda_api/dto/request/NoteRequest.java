package com.seyitkarahan.akilli_ajanda_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    private String title;
    private String content;
    private String color;
    private boolean isPinned;
}
