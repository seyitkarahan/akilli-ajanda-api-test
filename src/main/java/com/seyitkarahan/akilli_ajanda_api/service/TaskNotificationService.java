package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.TaskNotificationRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.TaskNotificationResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Task;
import com.seyitkarahan.akilli_ajanda_api.entity.TaskNotification;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.NotificationNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.TaskNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedActionException;
import com.seyitkarahan.akilli_ajanda_api.exception.UserNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.TaskNotificationRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.TaskRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskNotificationService {

    private final TaskNotificationRepository taskNotificationRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskNotificationService(TaskNotificationRepository taskNotificationRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.taskNotificationRepository = taskNotificationRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskNotificationResponse> getAllTaskNotifications() {
        User user = getCurrentUser();
        return taskNotificationRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TaskNotificationResponse getTaskNotificationById(Long id) {
        User user = getCurrentUser();

        TaskNotification notification = taskNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Task notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        return mapToResponse(notification);
    }

    public TaskNotificationResponse createTaskNotification(TaskNotificationRequest request) {
        User user = getCurrentUser();

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        TaskNotification notification = TaskNotification.builder()
                .notifyAt(request.getNotifyAt())
                .task(task)
                .user(user)
                .isSent(false)
                .build();

        return mapToResponse(taskNotificationRepository.save(notification));
    }

    public TaskNotificationResponse updateTaskNotification(Long id, TaskNotificationRequest request) {
        User user = getCurrentUser();

        TaskNotification notification = taskNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Task notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        notification.setNotifyAt(request.getNotifyAt());
        notification.setSent(request.isSent());

        return mapToResponse(taskNotificationRepository.save(notification));
    }

    public void deleteTaskNotification(Long id) {
        User user = getCurrentUser();

        TaskNotification notification = taskNotificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException("Not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Unauthorized");
        }

        taskNotificationRepository.delete(notification);
    }

    private TaskNotificationResponse mapToResponse(TaskNotification n) {
        return TaskNotificationResponse.builder()
                .id(n.getId())
                .notifyAt(n.getNotifyAt())
                .isSent(n.isSent())
                .taskId(n.getTask().getId())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authentication user not found"));
    }
}
