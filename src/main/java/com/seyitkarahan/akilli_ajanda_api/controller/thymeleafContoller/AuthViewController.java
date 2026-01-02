package com.seyitkarahan.akilli_ajanda_api.controller.thymeleafContoller;

import com.seyitkarahan.akilli_ajanda_api.dto.request.AuthRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.request.LoginRequest;
import com.seyitkarahan.akilli_ajanda_api.dto.response.AuthResponse;
import com.seyitkarahan.akilli_ajanda_api.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthViewController {

    private final AuthService authService;

    public AuthViewController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        HttpServletResponse response,
                        Model model) {
        try {
            AuthResponse authResponse = authService.login(request);

            Cookie jwtCookie = new Cookie("JWT", authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "E-posta veya şifre hatalı");
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute AuthRequest request,
                           HttpServletResponse response,
                           Model model) {
        try {
            AuthResponse authResponse = authService.register(request);

            Cookie jwtCookie = new Cookie("JWT", authResponse.getToken());
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            return "redirect:/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
