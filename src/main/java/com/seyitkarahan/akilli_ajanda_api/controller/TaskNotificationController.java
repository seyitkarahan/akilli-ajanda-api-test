package com.seyitkarahan.akilli_ajanda_api.controller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.TaskNotificationRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.TaskNotificationResponse;
import com.seyitkarahan.akilli_ajanda_api.service.TaskNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications/tasks")
public class TaskNotificationController {

    private final TaskNotificationService taskNotificationService;

    public TaskNotificationController(TaskNotificationService taskNotificationService) {
        this.taskNotificationService = taskNotificationService;
    }

    @GetMapping
    public ResponseEntity<List<TaskNotificationResponse>> getAllTaskNotifications() {
        return ResponseEntity.ok(taskNotificationService.getAllTaskNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskNotificationResponse> getTaskNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(taskNotificationService.getTaskNotificationById(id));
    }

    @PostMapping
    public ResponseEntity<TaskNotificationResponse> createTaskNotification(
            @RequestBody TaskNotificationRequest request
    ) {
        return ResponseEntity.ok(taskNotificationService.createTaskNotification(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskNotificationResponse> updateTaskNotification(
            @PathVariable Long id,
            @RequestBody TaskNotificationRequest request
    ) {
        return ResponseEntity.ok(taskNotificationService.updateTaskNotification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskNotification(@PathVariable Long id) {
        taskNotificationService.deleteTaskNotification(id);
        return ResponseEntity.noContent().build();
    }
}
