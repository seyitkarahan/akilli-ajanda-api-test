package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.EventRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.EventResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Category;
import com.seyitkarahan.akilli_ajanda_api.entity.Event;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.repository.CategoryRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.EventRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<EventResponse> getAllEvents(Long categoryId) {
        User user = getCurrentUser();
        List<Event> events = eventRepository.findByUser(user);

        if (categoryId != null) {
            return events.stream()
                    .filter(event -> event.getCategory() != null && event.getCategory().getId().equals(categoryId))
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return events.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(Long id) {
        User user = getCurrentUser();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to access this event");
        }

        return mapToResponse(event);
    }

    public EventResponse createEvent(EventRequest request) {
        User user = getCurrentUser();

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("You cannot assign a event to this category");
            }

            event.setCategory(category);
        }

        return mapToResponse(eventRepository.save(event));
    }

    public EventResponse updateEvent(Long id, EventRequest request) {
        User user = getCurrentUser();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to update this event");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setLocation(request.getLocation());
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());


        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("You cannot assign a event to this category");
            }

            event.setCategory(category);
        } else {
            event.setCategory(null);
        }

        return mapToResponse(eventRepository.save(event));
    }

    public void deleteEvent(Long id) {
        User user = getCurrentUser();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not allowed to delete this event");
        }

        eventRepository.delete(event);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private EventResponse mapToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .userId(event.getUser().getId())
                .categoryId(event.getCategory() != null ? event.getCategory().getId() : null)
                .build();
    }
}
