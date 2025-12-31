package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.CategoryRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.CategoryResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Category;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.CategoryNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedActionException;
import com.seyitkarahan.akilli_ajanda_api.exception.UserNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.CategoryRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        User user = getCurrentUser();
        return categoryRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        User user = getCurrentUser();
        Category category = Category.builder()
                .name(request.getName())
                .user(user)
                .build();

        return mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        User user = getCurrentUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Kategori bulunamadı!"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Bu kategoriyi güncelleme iznin yok!");
        }

        category.setName(request.getName());

        return mapToResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        User user = getCurrentUser();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Kategori bulunamadı!"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("Bu todo'yu silme iznin yok!");
        }
        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .userId(category.getUser().getId())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + email));
    }
}
