package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.entity.RecurringTaskRule;
import com.seyitkarahan.akilli_ajanda_api.exception.*;
import com.seyitkarahan.akilli_ajanda_api.repository.RecurringTaskRuleRepository;
import org.springframework.stereotype.Service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.TaskRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.TaskResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Category;
import com.seyitkarahan.akilli_ajanda_api.entity.Task;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.repository.CategoryRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.TaskRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RecurringTaskRuleRepository recurringTaskRuleRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       CategoryRepository categoryRepository, RecurringTaskRuleRepository recurringTaskRuleRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.recurringTaskRuleRepository = recurringTaskRuleRepository;
    }

    public List<TaskResponse> getAllTasks(Long categoryId) {
        User user = getCurrentUser();
        List<Task> tasks = taskRepository.findByUser(user);

        if (categoryId != null) {
            return tasks.stream()
                    .filter(task -> task.getCategory() != null && task.getCategory().getId().equals(categoryId))
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedTaskAccessException("You are not allowed to access this task");
        }
        return mapToResponse(task);
    }

    public TaskResponse createTask(TaskRequest request) {
        User user = getCurrentUser();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .importanceLevel(request.getImportanceLevel())
                .user(user)
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

            if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
                throw new UnauthorizedTaskAccessException("You cannot assign a task to this category");
            }

            task.setCategory(category);
        }

        if (request.getRecurringRuleId() != null) {
            RecurringTaskRule rule = recurringTaskRuleRepository.findById(request.getRecurringRuleId())
                    .orElseThrow(() -> new RecurringTaskRuleNotFoundException("Recurring task rule not found"));
            task.setRecurringRule(rule);
        }

        return mapToResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedTaskAccessException("You are not allowed to update this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setStartTime(request.getStartTime());
        task.setEndTime(request.getEndTime());
        task.setImportanceLevel(request.getImportanceLevel());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
                throw new CategoryNotFoundException("You cannot assign a task to this category");
            }
            task.setCategory(category);
        } else {
            task.setCategory(null);
        }

        if (request.getRecurringRuleId() != null) {
            RecurringTaskRule rule = recurringTaskRuleRepository.findById(request.getRecurringRuleId())
                    .orElseThrow(() -> new RecurringTaskRuleNotFoundException("Recurring task rule not found"));
            task.setRecurringRule(rule);
        } else {
            task.setRecurringRule(null);
        }

        return mapToResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedTaskAccessException("You are not allowed to delete this task");
        }

        taskRepository.delete(task);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .importanceLevel(task.getImportanceLevel())
                .userId(task.getUser().getId())
                .categoryId(task.getCategory() != null ? task.getCategory().getId() : null)
                .recurringRuleId(task.getRecurringRule() != null ? task.getRecurringRule().getId() : null)
                .build();
    }
}
