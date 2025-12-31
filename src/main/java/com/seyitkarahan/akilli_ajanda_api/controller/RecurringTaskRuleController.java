package com.seyitkarahan.akilli_ajanda_api.controller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.RecurringTaskRuleRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.RecurringTaskRuleResponse;
import com.seyitkarahan.akilli_ajanda_api.service.RecurringTaskRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recurring-rules")
public class RecurringTaskRuleController {

    private final RecurringTaskRuleService recurringTaskRuleService;

    public RecurringTaskRuleController(RecurringTaskRuleService recurringTaskRuleService) {
        this.recurringTaskRuleService = recurringTaskRuleService;
    }

    @GetMapping
    public ResponseEntity<List<RecurringTaskRuleResponse>> getAllRecurringRules() {
        return ResponseEntity.ok(recurringTaskRuleService.getAllRecurringRules());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTaskRuleResponse> getRecurringRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(recurringTaskRuleService.getRecurringRuleById(id));
    }

    @PostMapping
    public ResponseEntity<RecurringTaskRuleResponse> createRecurringRule(@RequestBody RecurringTaskRuleRequest request) {
        return ResponseEntity.ok(recurringTaskRuleService.createRecurringRule(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringTaskRuleResponse> updateRecurringRule(@PathVariable Long id,
                                                                         @RequestBody RecurringTaskRuleRequest request) {
        return ResponseEntity.ok(recurringTaskRuleService.updateRecurringRule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecurringRule(@PathVariable Long id) {
        recurringTaskRuleService.deleteRecurringRule(id);
        return ResponseEntity.noContent().build();
    }
}
