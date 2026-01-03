package com.seyitkarahan.akilli_ajanda_api.controller.thymeleafContoller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.NoteRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.NoteResponse;
import com.seyitkarahan.akilli_ajanda_api.service.NoteService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteViewController {

    private final NoteService noteService;

    public NoteViewController(NoteService noteService) {
        this.noteService = noteService;
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }

    @GetMapping
    public String getUserNotes(Model model) {
        String email = getCurrentUserEmail();
        if (email != null) {
            List<NoteResponse> notes = noteService.getUserNotes(email);
            model.addAttribute("notes", notes);
        }
        model.addAttribute("noteRequest", new NoteRequest()); // For the form
        return "notes";
    }

    @PostMapping
    public String createNote(@ModelAttribute("noteRequest") NoteRequest request) {
        String email = getCurrentUserEmail();
        if (email != null) {
            noteService.createNote(email, request);
        }
        return "redirect:/notes";
    }

    @PostMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id) {
        String email = getCurrentUserEmail();
        if (email != null) {
            noteService.deleteNote(id, email);
        }
        return "redirect:/notes";
    }
}
