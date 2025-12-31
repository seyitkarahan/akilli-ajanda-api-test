package com.seyitkarahan.akilli_ajanda_api.controller;

import com.seyitkarahan.akilli_ajanda_api.dto.response.ImageResponse;
import com.seyitkarahan.akilli_ajanda_api.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ImageResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long eventId
    ) {
        return ResponseEntity.ok(imageService.uploadImage(file, taskId, eventId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ImageResponse>> getMyImages() {
        return ResponseEntity.ok(imageService.getMyImages());
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<ImageResponse>> getImagesByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(imageService.getImagesByTask(taskId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ImageResponse>> getImagesByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(imageService.getImagesByEvent(eventId));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
