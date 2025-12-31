package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.CategoryRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.CategoryResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.Category;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.exception.CategoryNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.exception.UnauthorizedActionException;
import com.seyitkarahan.akilli_ajanda_api.repository.CategoryRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("seyit@test.com")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ---------------- GET ALL ----------------

    @Test
    void getAllCategories_shouldReturnUserCategories() {
        // given
        Category category1 = Category.builder()
                .id(1L)
                .name("Work")
                .user(user)
                .build();

        Category category2 = Category.builder()
                .id(2L)
                .name("Personal")
                .user(user)
                .build();

        when(categoryRepository.findByUser(user))
                .thenReturn(List.of(category1, category2));

        // when
        List<CategoryResponse> responses = categoryService.getAllCategories();

        // then
        assertEquals(2, responses.size());
        assertEquals("Work", responses.get(0).getName());
        verify(categoryRepository).findByUser(user);
    }

    // ---------------- CREATE ----------------

    @Test
    void createCategory_shouldCreateAndReturnCategory() {
        // given
        CategoryRequest request = new CategoryRequest("Work");

        Category savedCategory = Category.builder()
                .id(1L)
                .name("Work")
                .user(user)
                .build();

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(savedCategory);

        // when
        CategoryResponse response = categoryService.createCategory(request);

        // then
        assertNotNull(response);
        assertEquals("Work", response.getName());
        assertEquals(user.getId(), response.getUserId());
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateCategory_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.updateCategory(1L, new CategoryRequest("New")));
    }

    @Test
    void updateCategory_shouldThrowException_whenUnauthorized() {
        User anotherUser = User.builder()
                .id(2L)
                .email("other@test.com")
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Work")
                .user(anotherUser)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        assertThrows(UnauthorizedActionException.class,
                () -> categoryService.updateCategory(1L, new CategoryRequest("New")));
    }

    @Test
    void updateCategory_shouldUpdateCategorySuccessfully() {
        Category category = Category.builder()
                .id(1L)
                .name("Old")
                .user(user)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CategoryResponse response =
                categoryService.updateCategory(1L, new CategoryRequest("New"));

        assertEquals("New", response.getName());
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteCategory_shouldThrowException_whenCategoryNotFound() {
        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(1L));
    }

    @Test
    void deleteCategory_shouldThrowException_whenUnauthorized() {
        User anotherUser = User.builder()
                .id(2L)
                .build();

        Category category = Category.builder()
                .id(1L)
                .user(anotherUser)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        assertThrows(UnauthorizedActionException.class,
                () -> categoryService.deleteCategory(1L));
    }

    @Test
    void deleteCategory_shouldDeleteCategorySuccessfully() {
        Category category = Category.builder()
                .id(1L)
                .user(user)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }
}
