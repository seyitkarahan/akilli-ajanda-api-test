package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.TagRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.TagResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Tag;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.repository.TagRepository;
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
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TagService tagService;

    private User user;
    private Tag tag;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        tag = Tag.builder()
                .id(10L)
                .name("Urgent")
                .color("#FF0000")
                .user(user)
                .build();
    }

    @Test
    void createTag_shouldCreateTagSuccessfully() {
        TagRequest request = TagRequest.builder()
                .name("Urgent")
                .color("#FF0000")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        TagResponse response = tagService.createTag(request, 1L);

        assertNotNull(response);
        assertEquals("Urgent", response.getName());
        assertEquals("#FF0000", response.getColor());
        verify(userRepository).findById(1L);
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void createTag_shouldThrowException_whenUserNotFound() {
        TagRequest request = TagRequest.builder().name("Test").build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.createTag(request, 1L));
    }

    @Test
    void getAllTags_shouldReturnList() {
        when(tagRepository.findByUserId(1L)).thenReturn(List.of(tag));

        List<TagResponse> responses = tagService.getAllTags(1L);

        assertEquals(1, responses.size());
        assertEquals("Urgent", responses.get(0).getName());
    }

    @Test
    void getTagById_shouldReturnTag() {
        when(tagRepository.findById(10L)).thenReturn(Optional.of(tag));

        TagResponse response = tagService.getTagById(10L);

        assertEquals(10L, response.getId());
    }

    @Test
    void getTagById_shouldThrowException_whenNotFound() {
        when(tagRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.getTagById(10L));
    }

    @Test
    void updateTag_shouldUpdateSuccessfully() {
        TagRequest request = TagRequest.builder()
                .name("Updated Name")
                .color("#00FF00")
                .build();

        when(tagRepository.findById(10L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenAnswer(inv -> inv.getArgument(0));

        TagResponse response = tagService.updateTag(10L, request);

        assertEquals("Updated Name", response.getName());
        assertEquals("#00FF00", response.getColor());
    }

    @Test
    void deleteTag_shouldDeleteSuccessfully() {
        when(tagRepository.existsById(10L)).thenReturn(true);

        tagService.deleteTag(10L);

        verify(tagRepository).deleteById(10L);
    }

    @Test
    void deleteTag_shouldThrowException_whenNotFound() {
        when(tagRepository.existsById(10L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> tagService.deleteTag(10L));
    }
}
