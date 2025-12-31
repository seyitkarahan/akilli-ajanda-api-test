package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.response.ImageResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.ImageFile;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedActionException;
import com.seyitkarahan.akilli_ajanda_api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageFileRepository imageFileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private ImageService imageService;

    private User user;
    private ImageFile imageFile;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .build();

        imageFile = ImageFile.builder()
                .id(1L)
                .fileName("test.png")
                .fileType("image/png")
                .filePath("/images/test.png")
                .user(user)
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        );
    }

    // =========================
    // getMyImages
    // =========================
    @Test
    void getMyImages_shouldReturnImages() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(imageFileRepository.findByUser(user))
                .thenReturn(List.of(imageFile));

        List<ImageResponse> result = imageService.getMyImages();

        assertEquals(1, result.size());
        assertEquals("test.png", result.get(0).getFileName());
    }

    // =========================
    // getImagesByTask
    // =========================
    @Test
    void getImagesByTask_shouldReturnImages() {
        when(imageFileRepository.findByTaskId(1L))
                .thenReturn(List.of(imageFile));

        List<ImageResponse> result = imageService.getImagesByTask(1L);

        assertEquals(1, result.size());
        assertEquals("test.png", result.get(0).getFileName());
    }

    // =========================
    // getImagesByEvent
    // =========================
    @Test
    void getImagesByEvent_shouldReturnImages() {
        when(imageFileRepository.findByEventId(1L))
                .thenReturn(List.of(imageFile));

        List<ImageResponse> result = imageService.getImagesByEvent(1L);

        assertEquals(1, result.size());
        assertEquals("test.png", result.get(0).getFileName());
    }

    // =========================
    // deleteImage - SUCCESS
    // =========================
    @Test
    void deleteImage_shouldDeleteWhenUserIsOwner() {
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(imageFileRepository.findById(1L))
                .thenReturn(Optional.of(imageFile));

        imageService.deleteImage(1L);

        verify(imageFileRepository).delete(imageFile);
    }

    // =========================
    // deleteImage - UNAUTHORIZED
    // =========================
    @Test
    void deleteImage_shouldThrowExceptionWhenUserIsNotOwner() {
        User anotherUser = User.builder()
                .id(2L)
                .email("other@test.com")
                .build();

        imageFile.setUser(anotherUser);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(imageFileRepository.findById(1L))
                .thenReturn(Optional.of(imageFile));

        assertThrows(UnauthorizedActionException.class,
                () -> imageService.deleteImage(1L));
    }
}
