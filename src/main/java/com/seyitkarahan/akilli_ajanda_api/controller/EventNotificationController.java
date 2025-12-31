package com.seyitkarahan.akilli_ajanda_api.controller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.EventNotificationRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.EventNotificationResponse;
import com.seyitkarahan.akilli_ajanda_api.service.EventNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications/events")
public class EventNotificationController {

    private final EventNotificationService eventNotificationService;

    public EventNotificationController(EventNotificationService eventNotificationService) {
        this.eventNotificationService = eventNotificationService;
    }

    @GetMapping
    public ResponseEntity<List<EventNotificationResponse>> getAll() {
        return ResponseEntity.ok(eventNotificationService.getAllEventNotifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventNotificationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventNotificationService.getEventNotificationById(id));
    }

    @PostMapping
    public ResponseEntity<EventNotificationResponse> create(
            @RequestBody EventNotificationRequest request
    ) {
        return ResponseEntity.ok(eventNotificationService.createEventNotification(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventNotificationResponse> update(
            @PathVariable Long id,
            @RequestBody EventNotificationRequest request
    ) {
        return ResponseEntity.ok(eventNotificationService.updateEventNotification(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventNotificationService.deleteEventNotification(id);
        return ResponseEntity.noContent().build();
    }
}
