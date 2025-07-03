package com.example.director.controller;

import com.example.director.model.Scenario;
import com.example.director.model.User;
import com.example.director.repository.ScenarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class ScenarioController {

    @Autowired
    private ScenarioRepository scenarioRepository;

    @GetMapping("/scenario/create")
    public String createScenarioForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }
        return "scenario/create";
    }

    @PostMapping("/scenario/create")
    public String createScenario(@RequestParam String title, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "タイトルを入力してください");
        }

        Scenario scenario = new Scenario();
        scenario.setUserId(user.getId());
        scenario.setTitle(title);
        scenario.setBody(""); // 初期化
        scenarioRepository.save(scenario);

        return "redirect:/scenario/edit/" + scenario.getId();
    }

    @GetMapping("/scenario/edit/{id}")
    public String editScenario(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }

        Optional<Scenario> optionalScenario = scenarioRepository.findById(id);
        if (optionalScenario.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "シナリオが見つかりません");
        }

        Scenario scenario = optionalScenario.get();

        // セッションユーザーと一致しない場合は403
        if (!scenario.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "このシナリオにアクセスする権限がありません");
        }

        model.addAttribute("scenario", scenario);
        return "scenario/edit";
    }

    @PostMapping("/scenario/update")
    @ResponseBody
    public String updateScenario(@RequestParam Long id,
                                 @RequestParam String title,
                                 @RequestParam String body,
                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "NG:login_required";
        }

        Optional<Scenario> optionalScenario = scenarioRepository.findById(id);
        if (optionalScenario.isEmpty()) {
            return "NG:not_found";
        }

        Scenario scenario = optionalScenario.get();
        if (!scenario.getUserId().equals(user.getId())) {
            return "NG:no_permission";
        }

        scenario.setTitle(title);
        scenario.setBody(body);
        scenarioRepository.save(scenario);
        return "OK";
    }
}
