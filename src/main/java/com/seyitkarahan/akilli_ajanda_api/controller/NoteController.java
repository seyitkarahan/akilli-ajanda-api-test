package com.seyitkarahan.akilli_ajanda_api.controller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.NoteRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.NoteResponse;
import com.seyitkarahan.akilli_ajanda_api.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @PostMapping
    public ResponseEntity<NoteResponse> createNote(@RequestBody NoteRequest request) {
        return new ResponseEntity<>(noteService.createNote(getCurrentUserEmail(), request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getUserNotes() {
        return ResponseEntity.ok(noteService.getUserNotes(getCurrentUserEmail()));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Long noteId) {
        return ResponseEntity.ok(noteService.getNoteById(noteId, getCurrentUserEmail()));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponse> updateNote(@PathVariable Long noteId, @RequestBody NoteRequest request) {
        return ResponseEntity.ok(noteService.updateNote(noteId, getCurrentUserEmail(), request));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long noteId) {
        noteService.deleteNote(noteId, getCurrentUserEmail());
        return ResponseEntity.noContent().build();
    }
}
