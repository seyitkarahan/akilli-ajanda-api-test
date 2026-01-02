package com.seyitkarahan.akilli_ajanda_api.controller.thymeleafContoller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.CategoryRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.CategoryResponse;
import com.seyitkarahan.akilli_ajanda_api.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryViewController {

    private final CategoryService categoryService;

    public CategoryViewController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String categoriesPage(Model model) {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("categoryRequest", new CategoryRequest());
        return "categories";
    }

    @PostMapping
    public String createCategory(@ModelAttribute CategoryRequest request) {
        categoryService.createCategory(request);
        return "redirect:/categories";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @ModelAttribute CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }
}
