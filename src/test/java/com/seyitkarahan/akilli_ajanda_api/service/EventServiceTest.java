package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.EventRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.EventResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Category;
import com.seyitkarahan.akilli_ajanda_api.entity.Event;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.repository.CategoryRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.EventRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private EventService eventService;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("seyit@test.com")
                .build();

        category = Category.builder()
                .id(10L)
                .name("Work")
                .user(user)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ---------------- GET ALL ----------------

    @Test
    void getAllEvents_shouldReturnAllEvents() {
        Event e1 = Event.builder().id(1L).user(user).build();
        Event e2 = Event.builder().id(2L).user(user).build();

        when(eventRepository.findByUser(user))
                .thenReturn(List.of(e1, e2));

        List<EventResponse> responses = eventService.getAllEvents(null);

        assertEquals(2, responses.size());
    }

    @Test
    void getAllEvents_shouldFilterByCategory() {
        Event e1 = Event.builder().id(1L).user(user).category(category).build();
        Event e2 = Event.builder().id(2L).user(user).build();

        when(eventRepository.findByUser(user))
                .thenReturn(List.of(e1, e2));

        List<EventResponse> responses =
                eventService.getAllEvents(category.getId());

        assertEquals(1, responses.size());
        assertEquals(category.getId(), responses.get(0).getCategoryId());
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getEventById_shouldThrowException_whenNotFound() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> eventService.getEventById(1L));
    }

    @Test
    void getEventById_shouldThrowException_whenUnauthorized() {
        User anotherUser = User.builder().id(2L).build();

        Event event = Event.builder()
                .id(1L)
                .user(anotherUser)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        assertThrows(RuntimeException.class,
                () -> eventService.getEventById(1L));
    }

    @Test
    void getEventById_shouldReturnEvent() {
        Event event = Event.builder()
                .id(1L)
                .user(user)
                .category(category)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        EventResponse response = eventService.getEventById(1L);

        assertEquals(1L, response.getId());
        assertEquals(category.getId(), response.getCategoryId());
    }

    // ---------------- CREATE ----------------

    @Test
    void createEvent_shouldCreateWithoutCategory() {
        EventRequest request = EventRequest.builder()
                .title("Meeting")
                .build();

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        EventResponse response = eventService.createEvent(request);

        assertEquals("Meeting", response.getTitle());
        assertNull(response.getCategoryId());
    }

    @Test
    void createEvent_shouldThrowException_whenCategoryNotFound() {
        EventRequest request = EventRequest.builder()
                .categoryId(10L)
                .build();

        when(categoryRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> eventService.createEvent(request));
    }

    @Test
    void createEvent_shouldThrowException_whenUnauthorizedCategory() {
        User anotherUser = User.builder().id(2L).build();

        Category anotherCategory = Category.builder()
                .id(10L)
                .user(anotherUser)
                .build();

        EventRequest request = EventRequest.builder()
                .categoryId(10L)
                .build();

        when(categoryRepository.findById(10L))
                .thenReturn(Optional.of(anotherCategory));

        assertThrows(RuntimeException.class,
                () -> eventService.createEvent(request));
    }

    @Test
    void createEvent_shouldCreateWithCategory() {
        EventRequest request = EventRequest.builder()
                .title("Meeting")
                .categoryId(10L)
                .build();

        when(categoryRepository.findById(10L))
                .thenReturn(Optional.of(category));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        EventResponse response = eventService.createEvent(request);

        assertEquals(category.getId(), response.getCategoryId());
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateEvent_shouldThrowException_whenNotFound() {
        when(eventRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> eventService.updateEvent(1L, EventRequest.builder().build()));
    }

    @Test
    void updateEvent_shouldUpdateSuccessfully() {
        Event event = Event.builder()
                .id(1L)
                .user(user)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(eventRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        EventRequest request = EventRequest.builder()
                .title("Updated")
                .build();

        EventResponse response = eventService.updateEvent(1L, request);

        assertEquals("Updated", response.getTitle());
    }

    @Test
    void updateEvent_shouldRemoveCategory() {
        Event event = Event.builder()
                .id(1L)
                .user(user)
                .category(category)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(eventRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        EventResponse response =
                eventService.updateEvent(1L, EventRequest.builder().build());

        assertNull(response.getCategoryId());
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteEvent_shouldDeleteSuccessfully() {
        Event event = Event.builder()
                .id(1L)
                .user(user)
                .build();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        eventService.deleteEvent(1L);

        verify(eventRepository).delete(event);
    }
}
