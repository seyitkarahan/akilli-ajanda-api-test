package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.EventRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.EventResponse;
import com.seyitkarahan.akilli_ajanda_api.dto.response.TagResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Category;
import com.seyitkarahan.akilli_ajanda_api.entity.Event;
import com.seyitkarahan.akilli_ajanda_api.entity.Tag;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.repository.CategoryRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.EventRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.TagRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository,
                        TagRepository tagRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
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

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            // Validate tags belong to user
            for (Tag tag : tags) {
                if (!tag.getUser().getId().equals(user.getId())) {
                    throw new RuntimeException("You cannot assign a tag that does not belong to you");
                }
            }
            event.setTags(new HashSet<>(tags));
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

        if (request.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            // Validate tags belong to user
            for (Tag tag : tags) {
                if (!tag.getUser().getId().equals(user.getId())) {
                    throw new RuntimeException("You cannot assign a tag that does not belong to you");
                }
            }
            event.setTags(new HashSet<>(tags));
        } else {
            event.getTags().clear();
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
        Set<TagResponse> tagResponses = event.getTags().stream()
                .map(tag -> TagResponse.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .color(tag.getColor())
                        .userId(tag.getUser().getId())
                        .build())
                .collect(Collectors.toSet());

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
                .tags(tagResponses)
                .build();
    }
}
