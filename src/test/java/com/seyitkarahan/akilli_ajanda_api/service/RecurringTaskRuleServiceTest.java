package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.RecurringTaskRuleRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.RecurringTaskRuleResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.RecurringTaskRule;
import com.seyitkarahan.akilli_ajanda_api.enums.DayOfWeek;
import com.seyitkarahan.akilli_ajanda_api.enums.Frequency;
import com.seyitkarahan.akilli_ajanda_api.exception.RecurringTaskRuleNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.RecurringTaskRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecurringTaskRuleServiceTest {

    @Mock
    private RecurringTaskRuleRepository recurringTaskRuleRepository;

    @InjectMocks
    private RecurringTaskRuleService recurringTaskRuleService;

    @Test
    void getAllRecurringRules_shouldReturnList() {
        RecurringTaskRule rule = RecurringTaskRule.builder()
                .id(1L)
                .frequency(Frequency.WEEKLY)
                .dayOfWeek(DayOfWeek.MONDAY)
                .time("09:00")
                .build();

        when(recurringTaskRuleRepository.findAll())
                .thenReturn(List.of(rule));

        List<RecurringTaskRuleResponse> responses =
                recurringTaskRuleService.getAllRecurringRules();

        assertEquals(1, responses.size());
        assertEquals(Frequency.WEEKLY, responses.get(0).getFrequency());
        assertEquals(DayOfWeek.MONDAY, responses.get(0).getDayOfWeek());
        assertEquals("09:00", responses.get(0).getTime());
    }

    @Test
    void getRecurringRuleById_shouldReturnRule() {
        RecurringTaskRule rule = RecurringTaskRule.builder()
                .id(1L)
                .frequency(Frequency.WEEKLY)
                .dayOfWeek(DayOfWeek.MONDAY)
                .time("09:00")
                .build();

        when(recurringTaskRuleRepository.findById(1L))
                .thenReturn(Optional.of(rule));

        RecurringTaskRuleResponse response =
                recurringTaskRuleService.getRecurringRuleById(1L);

        assertEquals(Frequency.WEEKLY, response.getFrequency());
        assertEquals(DayOfWeek.MONDAY, response.getDayOfWeek());
        assertEquals("09:00", response.getTime());
    }

    @Test
    void getRecurringRuleById_shouldThrowException_whenNotFound() {
        when(recurringTaskRuleRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RecurringTaskRuleNotFoundException.class,
                () -> recurringTaskRuleService.getRecurringRuleById(1L)
        );
    }

    @Test
    void createRecurringRule_shouldSaveAndReturnRule() {
        RecurringTaskRuleRequest request = RecurringTaskRuleRequest.builder()
                .frequency(Frequency.WEEKLY)
                .dayOfWeek(DayOfWeek.MONDAY)
                .time("09:00")
                .build();

        RecurringTaskRule savedRule = RecurringTaskRule.builder()
                .id(1L)
                .frequency(Frequency.WEEKLY)
                .dayOfWeek(DayOfWeek.MONDAY)
                .time("09:00")
                .build();

        when(recurringTaskRuleRepository.save(any(RecurringTaskRule.class)))
                .thenReturn(savedRule);

        RecurringTaskRuleResponse response =
                recurringTaskRuleService.createRecurringRule(request);

        assertEquals(Frequency.WEEKLY, response.getFrequency());
        assertEquals(DayOfWeek.MONDAY, response.getDayOfWeek());
        assertEquals("09:00", response.getTime());
    }

    @Test
    void updateRecurringRule_shouldUpdateRule() {
        RecurringTaskRule existingRule = RecurringTaskRule.builder()
                .id(1L)
                .frequency(Frequency.WEEKLY)
                .dayOfWeek(DayOfWeek.MONDAY)
                .time("09:00")
                .build();

        RecurringTaskRuleRequest request = RecurringTaskRuleRequest.builder()
                .frequency(Frequency.DAILY)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .time("10:00")
                .build();

        RecurringTaskRule updatedRule = RecurringTaskRule.builder()
                .id(1L)
                .frequency(Frequency.DAILY)
                .dayOfWeek(DayOfWeek.FRIDAY)
                .time("10:00")
                .build();

        when(recurringTaskRuleRepository.findById(1L))
                .thenReturn(Optional.of(existingRule));
        when(recurringTaskRuleRepository.save(existingRule))
                .thenReturn(updatedRule);

        RecurringTaskRuleResponse response =
                recurringTaskRuleService.updateRecurringRule(1L, request);

        assertEquals(Frequency.DAILY, response.getFrequency());
        assertEquals(DayOfWeek.FRIDAY, response.getDayOfWeek());
        assertEquals("10:00", response.getTime());
    }

    @Test
    void deleteRecurringRule_shouldDeleteRule() {
        RecurringTaskRule rule = RecurringTaskRule.builder()
                .id(1L)
                .build();

        when(recurringTaskRuleRepository.findById(1L))
                .thenReturn(Optional.of(rule));

        recurringTaskRuleService.deleteRecurringRule(1L);

        verify(recurringTaskRuleRepository).delete(rule);
    }
}
