package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.RecurringTaskRuleRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.RecurringTaskRuleResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.RecurringTaskRule;
import com.seyitkarahan.akilli_ajanda_api.exception.RecurringTaskRuleNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.RecurringTaskRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecurringTaskRuleService {

    private final RecurringTaskRuleRepository recurringTaskRuleRepository;

    public RecurringTaskRuleService(RecurringTaskRuleRepository recurringTaskRuleRepository) {
        this.recurringTaskRuleRepository = recurringTaskRuleRepository;
    }

    public List<RecurringTaskRuleResponse> getAllRecurringRules() {
        return recurringTaskRuleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RecurringTaskRuleResponse getRecurringRuleById(Long id) {
        RecurringTaskRule rule = recurringTaskRuleRepository.findById(id)
                .orElseThrow(() -> new RecurringTaskRuleNotFoundException("Recurring rule not found"));
        return mapToResponse(rule);
    }

    public RecurringTaskRuleResponse createRecurringRule(RecurringTaskRuleRequest request) {
        RecurringTaskRule rule = RecurringTaskRule.builder()
                .frequency(request.getFrequency())
                .dayOfWeek(request.getDayOfWeek())
                .time(request.getTime())
                .build();

        return mapToResponse(recurringTaskRuleRepository.save(rule));
    }

    public RecurringTaskRuleResponse updateRecurringRule(Long id, RecurringTaskRuleRequest request) {
        RecurringTaskRule rule = recurringTaskRuleRepository.findById(id)
                .orElseThrow(() -> new RecurringTaskRuleNotFoundException("Recurring rule not found"));

        rule.setFrequency(request.getFrequency());
        rule.setDayOfWeek(request.getDayOfWeek());
        rule.setTime(request.getTime());

        return mapToResponse(recurringTaskRuleRepository.save(rule));
    }

    public void deleteRecurringRule(Long id) {
        RecurringTaskRule rule = recurringTaskRuleRepository.findById(id)
                .orElseThrow(() -> new RecurringTaskRuleNotFoundException("Recurring rule not found"));
        recurringTaskRuleRepository.delete(rule);
    }

    private RecurringTaskRuleResponse mapToResponse(RecurringTaskRule rule) {
        return RecurringTaskRuleResponse.builder()
                .id(rule.getId())
                .frequency(rule.getFrequency())
                .dayOfWeek(rule.getDayOfWeek())
                .time(rule.getTime())
                .build();
    }
}
