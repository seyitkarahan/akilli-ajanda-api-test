package com.seyitkarahan.akilli_ajanda_api.controller.thymeleafContoller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.TaskRequest;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.enums.ImportanceLevel;
import com.seyitkarahan.akilli_ajanda_api.enums.TaskStatus;
import com.seyitkarahan.akilli_ajanda_api.exception.UserNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import com.seyitkarahan.akilli_ajanda_api.service.CategoryService;
import com.seyitkarahan.akilli_ajanda_api.service.TagService;
import com.seyitkarahan.akilli_ajanda_api.service.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    private final TaskService taskService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final UserRepository userRepository;

    public TaskViewController(TaskService taskService,
                              CategoryService categoryService,
                              TagService tagService,
                              UserRepository userRepository) {
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String tasksPage(Model model) {
        User user = getCurrentUser();
        model.addAttribute("tasks", taskService.getAllTasks(null));
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("tags", tagService.getAllTags(user.getId()));
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("importanceLevels", ImportanceLevel.values());
        model.addAttribute("taskRequest", new TaskRequest());
        return "tasks";
    }

    @PostMapping
    public String createTask(@ModelAttribute TaskRequest taskRequest) {
        taskService.createTask(taskRequest);
        return "redirect:/tasks";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute TaskRequest taskRequest) {
        taskService.updateTask(id, taskRequest);
        return "redirect:/tasks";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }
}
