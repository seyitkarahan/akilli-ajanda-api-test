package com.seyitkarahan.akilli_ajanda_api.controller.thymeleafContoller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.EventRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.CategoryResponse;
import com.seyitkarahan.akilli_ajanda_api.dto.response.EventResponse;
import com.seyitkarahan.akilli_ajanda_api.dto.response.TagResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.UserNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import com.seyitkarahan.akilli_ajanda_api.service.CategoryService;
import com.seyitkarahan.akilli_ajanda_api.service.EventService;
import com.seyitkarahan.akilli_ajanda_api.service.TagService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/events")
public class EventViewController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final UserRepository userRepository;

    public EventViewController(EventService eventService,
                               CategoryService categoryService,
                               TagService tagService, UserRepository userRepository) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.userRepository = userRepository;
    }

    /**
     * EVENTS PAGE
     */
    @GetMapping
    public String eventsPage(Model model) {
        User user = getCurrentUser();
        List<EventResponse> events = eventService.getAllEvents(null);
        List<CategoryResponse> categories = categoryService.getAllCategories();
        List<TagResponse> tags = tagService.getAllTags(user.getId());

        model.addAttribute("events", events);
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);

        return "event";
    }

    /**
     * CREATE EVENT
     */
    @PostMapping
    public String createEvent(EventRequest request,
                              @RequestParam(required = false) Set<Long> tagIds) {

        request.setTagIds(tagIds);
        eventService.createEvent(request);
        return "redirect:/events";
    }

    /**
     * UPDATE EVENT
     */
    @PostMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id,
                              EventRequest request,
                              @RequestParam(required = false) Set<Long> tagIds) {

        request.setTagIds(tagIds);
        eventService.updateEvent(id, request);
        return "redirect:/events";
    }

    /**
     * DELETE EVENT
     */
    @PostMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));
    }
}
