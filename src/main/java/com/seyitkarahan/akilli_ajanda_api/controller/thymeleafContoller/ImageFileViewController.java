package com.seyitkarahan.akilli_ajanda_api.controller.thymeleafContoller;

import com.seyitkarahan.akilli_ajanda_api.dto.response.ImageResponse;
import com.seyitkarahan.akilli_ajanda_api.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/image-files")
public class ImageFileViewController {

    private final ImageService imageService;

    public ImageFileViewController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public String getImageFiles(Model model) {
        List<ImageResponse> images = imageService.getMyImages();
        model.addAttribute("imageFiles", images);
        return "imagefile";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        imageService.uploadImage(file, null, null);
        return "redirect:/image-files";
    }

    @PostMapping("/delete/{id}")
    public String deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return "redirect:/image-files";
    }
}
