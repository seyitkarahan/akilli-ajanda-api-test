package com.seyitkarahan.akilli_ajanda_api.repository;

import com.seyitkarahan.akilli_ajanda_api.entity.RecurringTaskRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecurringTaskRuleRepository  extends JpaRepository<RecurringTaskRule, Long> {

    List<RecurringTaskRule> findByFrequency(String frequency);
}