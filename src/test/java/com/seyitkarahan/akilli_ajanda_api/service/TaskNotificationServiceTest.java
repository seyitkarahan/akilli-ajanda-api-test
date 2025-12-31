package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.TaskNotificationRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.TaskNotificationResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Task;
import com.seyitkarahan.akilli_ajanda_api.entity.TaskNotification;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.NotificationNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.TaskNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedActionException;
import com.seyitkarahan.akilli_ajanda_api.repository.TaskNotificationRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.TaskRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskNotificationServiceTest {

    @Mock
    private TaskNotificationRepository taskNotificationRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskNotificationService taskNotificationService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .build();

        task = Task.builder()
                .id(1L)
                .user(user)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        );

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllTaskNotifications_shouldReturnList() {
        TaskNotification notification = TaskNotification.builder()
                .id(1L)
                .user(user)
                .task(task)
                .notifyAt(LocalDateTime.now())
                .isSent(false)
                .build();

        when(taskNotificationRepository.findByUser(user))
                .thenReturn(List.of(notification));

        List<TaskNotificationResponse> responses =
                taskNotificationService.getAllTaskNotifications();

        assertEquals(1, responses.size());
        assertEquals(task.getId(), responses.get(0).getTaskId());
        assertFalse(responses.get(0).isSent());
    }

    @Test
    void getTaskNotificationById_shouldReturnNotification() {
        TaskNotification notification = TaskNotification.builder()
                .id(1L)
                .user(user)
                .task(task)
                .notifyAt(LocalDateTime.now())
                .isSent(false)
                .build();

        when(taskNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        TaskNotificationResponse response =
                taskNotificationService.getTaskNotificationById(1L);

        assertEquals(task.getId(), response.getTaskId());
        assertFalse(response.isSent());
    }

    @Test
    void getTaskNotificationById_shouldThrowUnauthorizedException() {
        User otherUser = User.builder().id(2L).build();

        TaskNotification notification = TaskNotification.builder()
                .id(1L)
                .user(otherUser)
                .task(task)
                .build();

        when(taskNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        assertThrows(
                UnauthorizedActionException.class,
                () -> taskNotificationService.getTaskNotificationById(1L)
        );
    }

    @Test
    void createTaskNotification_shouldCreateNotification() {
        TaskNotificationRequest request = TaskNotificationRequest.builder()
                .taskId(task.getId())
                .notifyAt(LocalDateTime.now())
                .build();

        when(taskRepository.findById(task.getId()))
                .thenReturn(Optional.of(task));

        when(taskNotificationRepository.save(any(TaskNotification.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        TaskNotificationResponse response =
                taskNotificationService.createTaskNotification(request);

        assertEquals(task.getId(), response.getTaskId());
        assertFalse(response.isSent());
    }

    @Test
    void createTaskNotification_shouldThrowTaskNotFound() {
        TaskNotificationRequest request = TaskNotificationRequest.builder()
                .taskId(99L)
                .build();

        when(taskRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> taskNotificationService.createTaskNotification(request)
        );
    }

    @Test
    void updateTaskNotification_shouldUpdateNotification() {
        TaskNotification notification = TaskNotification.builder()
                .id(1L)
                .user(user)
                .task(task)
                .isSent(false)
                .build();

        TaskNotificationRequest request = TaskNotificationRequest.builder()
                .notifyAt(LocalDateTime.now())
                .isSent(true)
                .build();

        when(taskNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));
        when(taskNotificationRepository.save(notification))
                .thenReturn(notification);

        TaskNotificationResponse response =
                taskNotificationService.updateTaskNotification(1L, request);

        assertTrue(response.isSent());
    }

    @Test
    void deleteTaskNotification_shouldDelete() {
        TaskNotification notification = TaskNotification.builder()
                .id(1L)
                .user(user)
                .task(task)
                .build();

        when(taskNotificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        taskNotificationService.deleteTaskNotification(1L);

        verify(taskNotificationRepository).delete(notification);
    }

    @Test
    void deleteTaskNotification_shouldThrowNotificationNotFound() {
        when(taskNotificationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotificationNotFoundException.class,
                () -> taskNotificationService.deleteTaskNotification(1L)
        );
    }
}
