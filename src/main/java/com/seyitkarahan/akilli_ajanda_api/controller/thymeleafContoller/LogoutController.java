package com.seyitkarahan.akilli_ajanda_api.controller.thymeleafContoller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {

    @PostMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {

        Cookie cookie = new Cookie("JWT", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        request.getSession().invalidate();
        SecurityContextHolder.clearContext();

        return "redirect:/login";
    }

}
