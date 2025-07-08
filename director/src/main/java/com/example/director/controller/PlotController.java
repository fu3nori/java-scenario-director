package com.example.director.controller;

import com.example.director.model.Plot;
import com.example.director.repository.PlotRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.director.model.User;

@Controller
@RequestMapping("/plot")
public class PlotController {

    @Autowired
    private PlotRepository plotRepository;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("plot", new Plot());
        return "plot/create";
    }

    @PostMapping("/create")
    public String createPlot(@ModelAttribute Plot plot, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        plot.setUserId(user.getId());
        Plot savedPlot = plotRepository.save(plot);
        return "redirect:/plot/edit/" + savedPlot.getId();
    }

}
