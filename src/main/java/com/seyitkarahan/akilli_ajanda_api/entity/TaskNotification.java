package com.seyitkarahan.akilli_ajanda_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "task_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TaskNotification extends BaseNotification {

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id")
    private Task task;
}
