package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.TaskRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.TaskResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Category;
import com.seyitkarahan.akilli_ajanda_api.entity.Task;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedTaskAccessException;
import com.seyitkarahan.akilli_ajanda_api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RecurringTaskRuleRepository recurringTaskRuleRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        task = Task.builder()
                .id(10L)
                .title("Test Task")
                .user(user)
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    // ---------------- GET ALL TASKS ----------------

    @Test
    void getAllTasks_shouldReturnTasks() {
        when(taskRepository.findByUser(user)).thenReturn(List.of(task));

        List<TaskResponse> result = taskService.getAllTasks(null);

        assertEquals(1, result.size());
        verify(taskRepository).findByUser(user);
    }

    // ---------------- GET BY ID ----------------

    @Test
    void getTaskById_whenAuthorized_shouldReturnTask() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(10L);

        assertEquals(task.getId(), response.getId());
    }

    @Test
    void getTaskById_whenUnauthorized_shouldThrowException() {
        User anotherUser = User.builder().id(99L).build();
        task.setUser(anotherUser);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedTaskAccessException.class,
                () -> taskService.getTaskById(10L));
    }

    // ---------------- CREATE TASK ----------------

    @Test
    void createTask_shouldCreateTaskSuccessfully() {
        TaskRequest request = TaskRequest.builder()
                .title("New Task")
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_withCategory_shouldAssignCategory() {
        Category category = Category.builder()
                .id(5L)
                .user(user)
                .build();

        TaskRequest request = TaskRequest.builder()
                .categoryId(5L)
                .build();

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(category));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        verify(categoryRepository).findById(5L);
    }

    // ---------------- UPDATE TASK ----------------

    @Test
    void updateTask_whenAuthorized_shouldUpdate() {
        TaskRequest request = TaskRequest.builder()
                .title("Updated")
                .build();

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponse response = taskService.updateTask(10L, request);

        assertEquals(task.getId(), response.getId());
    }

    // ---------------- DELETE TASK ----------------

    @Test
    void deleteTask_whenAuthorized_shouldDelete() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.deleteTask(10L);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_whenUnauthorized_shouldThrowException() {
        User anotherUser = User.builder().id(2L).build();
        task.setUser(anotherUser);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThrows(UnauthorizedTaskAccessException.class,
                () -> taskService.deleteTask(10L));
    }
}

