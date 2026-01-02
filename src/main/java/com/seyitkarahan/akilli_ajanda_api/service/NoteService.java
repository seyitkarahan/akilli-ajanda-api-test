package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.NoteRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.NoteResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Note;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.repository.NoteRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoteResponse createNote(String email, NoteRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .color(request.getColor())
                .isPinned(request.isPinned())
                .user(user)
                .build();

        Note savedNote = noteRepository.save(note);
        return mapToResponse(savedNote);
    }

    public List<NoteResponse> getUserNotes(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        
        return noteRepository.findByUserIdOrderByIsPinnedDescUpdatedAtDesc(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public NoteResponse getNoteById(Long noteId, String email) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + noteId));
        
        if (!note.getUser().getEmail().equals(email)) {
            throw new SecurityException("You are not authorized to access this note");
        }

        return mapToResponse(note);
    }

    @Transactional
    public NoteResponse updateNote(Long noteId, String email, NoteRequest request) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + noteId));

        if (!note.getUser().getEmail().equals(email)) {
            throw new SecurityException("You are not authorized to update this note");
        }

        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setColor(request.getColor());
        note.setPinned(request.isPinned());

        Note updatedNote = noteRepository.save(note);
        return mapToResponse(updatedNote);
    }

    @Transactional
    public void deleteNote(Long noteId, String email) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + noteId));

        if (!note.getUser().getEmail().equals(email)) {
            throw new SecurityException("You are not authorized to delete this note");
        }

        noteRepository.delete(note);
    }

    private NoteResponse mapToResponse(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .color(note.getColor())
                .isPinned(note.isPinned())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
