package com.seyitkarahan.akilli_ajanda_api.entity;

import com.seyitkarahan.akilli_ajanda_api.enums.DayOfWeek;
import com.seyitkarahan.akilli_ajanda_api.enums.Frequency;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ecurring_task_rules")
public class RecurringTaskRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Frequency frequency; // DAILY, WEEKLY, MONTHLY

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek; // MONDAY, TUESDAY...

    private String time;      // "09:00"
}
