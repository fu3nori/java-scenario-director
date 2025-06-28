package com.example.director.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HelloController {

    // /hello にアクセスしたときにビューを表示
    @GetMapping("/hello")
    public String showHallo(Model model) {
        model.addAttribute("message", "こんにちは、兄弟！");
        return "hello"; // resources/templates/hallo.html を指す
    }
}
