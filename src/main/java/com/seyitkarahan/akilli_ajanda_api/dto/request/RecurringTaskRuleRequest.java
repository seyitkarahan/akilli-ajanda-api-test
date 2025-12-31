package com.seyitkarahan.akilli_ajanda_api.dto.request;

import com.seyitkarahan.akilli_ajanda_api.enums.DayOfWeek;
import com.seyitkarahan.akilli_ajanda_api.enums.Frequency;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringTaskRuleRequest {

    private Frequency frequency;
    private DayOfWeek dayOfWeek;
    private String time;
}
