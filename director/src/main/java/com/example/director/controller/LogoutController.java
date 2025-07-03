package com.example.director.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            // ログインしていない状態でアクセスした場合、403を返す
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }

        // セッションを無効化してトップページへリダイレクト
        session.invalidate();
        return "redirect:/";
    }
}
