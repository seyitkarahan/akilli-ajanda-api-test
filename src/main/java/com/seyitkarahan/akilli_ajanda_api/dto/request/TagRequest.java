package com.seyitkarahan.akilli_ajanda_api.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequest {

    private String name;
    private String color;
}
