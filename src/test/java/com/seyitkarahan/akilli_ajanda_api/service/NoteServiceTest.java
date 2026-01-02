package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.NoteRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.NoteResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Note;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.repository.NoteRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @InjectMocks
    private NoteService noteService;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Note note;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        note = Note.builder()
                .id(10L)
                .title("Test Note")
                .content("Test Content")
                .user(user)
                .build();
    }

    // ---------------- CREATE NOTE ----------------

    @Test
    void createNote_shouldCreateNoteSuccessfully() {
        NoteRequest request = NoteRequest.builder()
                .title("New Note")
                .content("Content")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteResponse response = noteService.createNote(user.getEmail(), request);

        assertNotNull(response);
        verify(userRepository).findByEmail(user.getEmail());
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void createNote_whenUserNotFound_shouldThrowException() {
        NoteRequest request = NoteRequest.builder().build();
        when(userRepository.findByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> noteService.createNote("unknown@mail.com", request));
    }

    // ---------------- GET USER NOTES ----------------

    @Test
    void getUserNotes_shouldReturnNotes() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(noteRepository.findByUserIdOrderByIsPinnedDescUpdatedAtDesc(user.getId())).thenReturn(List.of(note));

        List<NoteResponse> result = noteService.getUserNotes(user.getEmail());

        assertEquals(1, result.size());
        verify(noteRepository).findByUserIdOrderByIsPinnedDescUpdatedAtDesc(user.getId());
    }

    // ---------------- GET NOTE BY ID ----------------

    @Test
    void getNoteById_whenAuthorized_shouldReturnNote() {
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        NoteResponse response = noteService.getNoteById(10L, user.getEmail());

        assertEquals(note.getId(), response.getId());
    }

    @Test
    void getNoteById_whenUnauthorized_shouldThrowException() {
        User anotherUser = User.builder().id(99L).email("other@mail.com").build();
        note.setUser(anotherUser);

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        assertThrows(SecurityException.class,
                () -> noteService.getNoteById(10L, user.getEmail()));
    }

    // ---------------- UPDATE NOTE ----------------

    @Test
    void updateNote_whenAuthorized_shouldUpdate() {
        NoteRequest request = NoteRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));
        when(noteRepository.save(note)).thenReturn(note);

        NoteResponse response = noteService.updateNote(10L, user.getEmail(), request);

        assertEquals(note.getId(), response.getId());
        verify(noteRepository).save(note);
    }

    @Test
    void updateNote_whenUnauthorized_shouldThrowException() {
        User anotherUser = User.builder().id(99L).email("other@mail.com").build();
        note.setUser(anotherUser);
        NoteRequest request = NoteRequest.builder().build();

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        assertThrows(SecurityException.class,
                () -> noteService.updateNote(10L, user.getEmail(), request));
    }

    // ---------------- DELETE NOTE ----------------

    @Test
    void deleteNote_whenAuthorized_shouldDelete() {
        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        noteService.deleteNote(10L, user.getEmail());

        verify(noteRepository).delete(note);
    }

    @Test
    void deleteNote_whenUnauthorized_shouldThrowException() {
        User anotherUser = User.builder().id(99L).email("other@mail.com").build();
        note.setUser(anotherUser);

        when(noteRepository.findById(10L)).thenReturn(Optional.of(note));

        assertThrows(SecurityException.class,
                () -> noteService.deleteNote(10L, user.getEmail()));
    }
}
