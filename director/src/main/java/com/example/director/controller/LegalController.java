package com.example.director.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/legal")
    public String legal() {
        return "legal";
    }
}
