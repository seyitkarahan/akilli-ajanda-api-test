package com.seyitkarahan.akilli_ajanda_api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private String filePath;
}
