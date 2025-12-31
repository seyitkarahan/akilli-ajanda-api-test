package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.EventNotificationRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.EventNotificationResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Event;
import com.seyitkarahan.akilli_ajanda_api.entity.EventNotification;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.EventNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.NotificationNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedActionException;
import com.seyitkarahan.akilli_ajanda_api.repository.EventNotificationRepository;
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
class EventNotificationServiceTest {

    @Mock
    private EventNotificationRepository eventNotificationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventNotificationService service;

    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("seyit@test.com")
                .build();

        event = Event.builder()
                .id(10L)
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
    void getAllEventNotifications_shouldReturnUserNotifications() {
        EventNotification n1 = EventNotification.builder()
                .id(1L)
                .user(user)
                .event(event)
                .isSent(false)
                .build();

        EventNotification n2 = EventNotification.builder()
                .id(2L)
                .user(user)
                .event(event)
                .isSent(true)
                .build();

        when(eventNotificationRepository.findByUser(user))
                .thenReturn(List.of(n1, n2));

        List<EventNotificationResponse> responses =
                service.getAllEventNotifications();

        assertEquals(2, responses.size());
        verify(eventNotificationRepository).findByUser(user);
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getEventNotificationById_shouldThrowException_whenNotFound() {
        when(eventNotificationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class,
                () -> service.getEventNotificationById(1L));
    }

    @Test
    void getEventNotificationById_shouldThrowException_whenUnauthorized() {
        User anotherUser = User.builder().id(2L).build();

        EventNotification notification = EventNotification.builder()
                .id(1L)
                .user(anotherUser)
                .event(event)
                .build();

        when(eventNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        assertThrows(UnauthorizedActionException.class,
                () -> service.getEventNotificationById(1L));
    }

    @Test
    void getEventNotificationById_shouldReturnNotification() {
        EventNotification notification = EventNotification.builder()
                .id(1L)
                .user(user)
                .event(event)
                .isSent(false)
                .build();

        when(eventNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        EventNotificationResponse response =
                service.getEventNotificationById(1L);

        assertEquals(1L, response.getId());
        assertEquals(event.getId(), response.getEventId());
    }

    // ---------------- CREATE ----------------

    @Test
    void createEventNotification_shouldThrowException_whenEventNotFound() {
        EventNotificationRequest request =
                new EventNotificationRequest(LocalDateTime.now(), false, 10L);

        when(eventRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class,
                () -> service.createEventNotification(request));
    }

    @Test
    void createEventNotification_shouldThrowException_whenUnauthorized() {
        User anotherUser = User.builder().id(2L).build();

        Event anotherEvent = Event.builder()
                .id(10L)
                .user(anotherUser)
                .build();

        EventNotificationRequest request =
                new EventNotificationRequest(LocalDateTime.now(), false,10L);

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(anotherEvent));

        assertThrows(UnauthorizedActionException.class,
                () -> service.createEventNotification(request));
    }

    @Test
    void createEventNotification_shouldCreateNotification() {
        EventNotificationRequest request =
                new EventNotificationRequest(LocalDateTime.now(), false, 10L);

        when(eventRepository.findById(10L))
                .thenReturn(Optional.of(event));

        when(eventNotificationRepository.save(any(EventNotification.class)))
                .thenAnswer(inv -> {
                    EventNotification n = inv.getArgument(0);
                    n.setId(1L);
                    return n;
                });

        EventNotificationResponse response =
                service.createEventNotification(request);

        assertEquals(event.getId(), response.getEventId());
        assertFalse(response.isSent());
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateEventNotification_shouldThrowException_whenNotFound() {
        when(eventNotificationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class,
                () -> service.updateEventNotification(1L,
                        new EventNotificationRequest(LocalDateTime.now(), true, 10L)));
    }

    @Test
    void updateEventNotification_shouldUpdateSuccessfully() {
        EventNotification notification = EventNotification.builder()
                .id(1L)
                .user(user)
                .event(event)
                .isSent(false)
                .build();

        when(eventNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        when(eventNotificationRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        EventNotificationResponse response =
                service.updateEventNotification(1L,
                        new EventNotificationRequest(LocalDateTime.now(), true, 10L));

        assertTrue(response.isSent());
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteEventNotification_shouldDeleteSuccessfully() {
        EventNotification notification = EventNotification.builder()
                .id(1L)
                .user(user)
                .event(event)
                .build();

        when(eventNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        service.deleteEventNotification(1L);

        verify(eventNotificationRepository).delete(notification);
    }
}
