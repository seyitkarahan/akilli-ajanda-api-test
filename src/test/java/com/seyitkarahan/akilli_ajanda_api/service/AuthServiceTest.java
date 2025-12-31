package com.seyitkarahan.akilli_ajanda_api.service;

import com.seyitkarahan.akilli_ajanda_api.dto.request.AuthRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.request.LoginRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.AuthResponse;
import com.seyitkarahan.akilli_ajanda_api.entity.User;
import com.seyitkarahan.akilli_ajanda_api.entity.UserSettings;
import com.seyitkarahan.akilli_ajanda_api.exception.UserAlreadyExistsException;
import com.seyitkarahan.akilli_ajanda_api.exception.UserNotFoundException;
import com.seyitkarahan.akilli_ajanda_api.repository.UserRepository;
import com.seyitkarahan.akilli_ajanda_api.repository.UserSettingsRepository;
import com.seyitkarahan.akilli_ajanda_api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    // ---------------- REGISTER TESTS ----------------

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        // given
        AuthRequest request = new AuthRequest(
                "Seyit",
                "seyit@test.com",
                "123456"
        );

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(true);

        // when & then
        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(userSettingsRepository, never()).save(any());
    }

    @Test
    void register_shouldCreateUserAndSettings_andReturnToken() {
        // given
        AuthRequest request = new AuthRequest(
                "Seyit",
                "seyit@test.com",
                "123456"
        );

        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encodedPassword");

        when(jwtService.generateToken(request.getEmail()))
                .thenReturn("jwt-token");

        // when
        AuthResponse response = authService.register(request);

        // then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).save(any(User.class));
        verify(userSettingsRepository).save(any(UserSettings.class));
        verify(jwtService).generateToken(request.getEmail());
    }

    // ---------------- LOGIN TESTS ----------------

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        // given
        LoginRequest request = new LoginRequest(
                "seyit@test.com",
                "123456"
        );

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class,
                () -> authService.login(request));
    }

    @Test
    void login_shouldAuthenticate_andReturnToken() {
        // given
        LoginRequest request = new LoginRequest(
                "seyit@test.com",
                "123456"
        );

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user.getEmail()))
                .thenReturn("jwt-token");

        // when
        AuthResponse response = authService.login(request);

        // then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user.getEmail());
    }

    @Test
    void register_shouldThrowException_whenNameIsNull() {
        AuthRequest request = new AuthRequest(null, "test@test.com", "123456");
        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
    }

    @Test
    void login_shouldThrowException_whenPasswordIsWrong() {
        LoginRequest request = new LoginRequest("test@test.com", "wrongpassword");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        doThrow(new RuntimeException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrowException_whenJwtGenerationFails() {
        LoginRequest request = new LoginRequest("test@test.com", "123456");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        doNothing().when(authenticationManager).authenticate(any());
        when(jwtService.generateToken(user.getEmail())).thenThrow(new RuntimeException("JWT error"));

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

}
