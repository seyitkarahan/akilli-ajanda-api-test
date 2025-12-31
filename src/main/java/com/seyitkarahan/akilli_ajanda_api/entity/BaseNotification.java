package com.seyitkarahan.akilli_ajanda_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime notifyAt;

    private boolean isSent = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
