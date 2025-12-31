package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.EventNotificationRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.EventNotificationResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Event;
import com.seyitkarahan.akilli_ajanda_api.entity.EventNotification;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.EventNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.NotificationNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedActionException;
import com.seyitkarahan.akilli_ajanda_api.exception.UserNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.EventNotificationRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.EventRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventNotificationService {

    private final EventNotificationRepository eventNotificationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventNotificationService(EventNotificationRepository eventNotificationRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.eventNotificationRepository = eventNotificationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public List<EventNotificationResponse> getAllEventNotifications() {
        User user = getCurrentUser();
        return eventNotificationRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EventNotificationResponse getEventNotificationById(Long id) {
        User user = getCurrentUser();

        EventNotification notification = eventNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Event notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        return mapToResponse(notification);
    }

    public EventNotificationResponse createEventNotification(EventNotificationRequest request) {
        User user = getCurrentUser();

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        EventNotification notification = EventNotification.builder()
                .notifyAt(request.getNotifyAt())
                .event(event)
                .user(user)
                .isSent(false)
                .build();

        return mapToResponse(eventNotificationRepository.save(notification));
    }

    public EventNotificationResponse updateEventNotification(Long id, EventNotificationRequest request) {
        User user = getCurrentUser();

        EventNotification notification = eventNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Event notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        notification.setNotifyAt(request.getNotifyAt());
        notification.setSent(request.isSent());

        return mapToResponse(eventNotificationRepository.save(notification));
    }

    public void deleteEventNotification(Long id) {
        User user = getCurrentUser();

        EventNotification notification = eventNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        eventNotificationRepository.delete(notification);
    }

    private EventNotificationResponse mapToResponse(EventNotification n) {
        return EventNotificationResponse.builder()
                .id(n.getId())
                .notifyAt(n.getNotifyAt())
                .isSent(n.isSent())
                .eventId(n.getEvent().getId())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
