package com.example.director.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CsrfTokenAdvice {

    @ModelAttribute("_csrf")
    public CsrfToken csrfToken(HttpServletRequest request) {
        // リクエスト属性から CsrfToken を取り出してモデルに載せる
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }

}
