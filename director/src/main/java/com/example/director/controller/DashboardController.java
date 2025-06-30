package com.example.director.controller;

import com.example.director.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // ログインしていない場合、403を返す
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }

        model.addAttribute("penName", user.getPenName());
        return "dashboard";
    }
}
