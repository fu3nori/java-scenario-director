package com.example.director.controller;

import com.example.director.model.User;
import com.example.director.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session) {
        User user = userService.login(email, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        }
        return "redirect:/?error=login";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String pen_name,
                         @RequestParam String email,
                         @RequestParam String password,
                         HttpSession session) {
        User newUser = new User();
        newUser.setPenName(pen_name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        User saved = userService.registerUser(newUser);
        session.setAttribute("user", saved);
        return "redirect:/dashboard";
    }
}
