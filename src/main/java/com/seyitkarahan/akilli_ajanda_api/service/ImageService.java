package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.response.ImageResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.*;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedActionException;
import com.seyitkarahan.akilli_ajanda_api.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ImageFileRepository imageFileRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EventRepository eventRepository;

    public ImageService(ImageFileRepository imageFileRepository,
                        UserRepository userRepository,
                        TaskRepository taskRepository,
                        EventRepository eventRepository) {
        this.imageFileRepository = imageFileRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    public ImageResponse uploadImage(MultipartFile file, Long taskId, Long eventId) {
        User user = getCurrentUser();

        try {
            Files.createDirectories(Path.of(uploadDir));

            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = UUID.randomUUID() + extension;
            String filePath = uploadDir + fileName;

            file.transferTo(new File(filePath));

            Task task = taskId != null ? taskRepository.findById(taskId).orElse(null) : null;
            Event event = eventId != null ? eventRepository.findById(eventId).orElse(null) : null;

            ImageFile image = ImageFile.builder()
                    .fileName(fileName)
                    .fileType(file.getContentType())
                    .filePath(filePath)
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .task(task)
                    .event(event)
                    .build();

            imageFileRepository.save(image);

            return mapToResponse(image);

        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<ImageResponse> getMyImages() {
        User user = getCurrentUser();
        return imageFileRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ImageResponse> getImagesByTask(Long taskId) {
        return imageFileRepository.findByTaskId(taskId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ImageResponse> getImagesByEvent(Long eventId) {
        return imageFileRepository.findByEventId(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteImage(Long imageId) {
        User user = getCurrentUser();

        ImageFile image = imageFileRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        if (!image.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You cannot delete this image");
        }

        File file = new File(image.getFilePath());
        if (file.exists()) {
            file.delete();
        }

        imageFileRepository.delete(image);
    }

    private ImageResponse mapToResponse(ImageFile image) {
        return ImageResponse.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .fileType(image.getFileType())
                .filePath(image.getFilePath())
                .build();
    }
}
