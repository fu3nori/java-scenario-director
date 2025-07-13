package com.example.director.controller;

import com.example.director.model.Plot;
import com.example.director.repository.PlotRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.director.model.User;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.example.director.model.PlotOrder;
import com.example.director.repository.PlotOrderRepository;
import java.util.List;

@Controller
@RequestMapping("/plot")

public class PlotController {


    @Autowired
    private PlotOrderRepository plotOrderRepository;

    @Autowired
    private PlotRepository plotRepository;

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }

        model.addAttribute("plot", new Plot());
        return "plot/create";
    }



    @PostMapping("/create")
    public String createPlot(@ModelAttribute Plot plot, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            // 未ログインなら 403 Forbidden を返す（redirectではなく）
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }

        plot.setUserId(user.getId());
        Plot savedPlot = plotRepository.save(plot);

        return "redirect:/plot/edit/" + savedPlot.getId();
    }

    // プロット編集
    @GetMapping("/edit/{id}")
    public String editPlot(@PathVariable("id") Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ログインが必要です");
        }

        // プロット取得（存在チェック）
        Plot plot = plotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "プロットが存在しません"));

        // 他人のプロットにアクセスしようとしているか確認
        if (!plot.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "このプロットにアクセスする権限がありません");
        }

        // plot_orders を取得（なければ初期データ1行追加）
        List<PlotOrder> orders = plotOrderRepository.findByPlotIdOrderByPlotOrderAsc(plot.getId());
        if (orders.isEmpty()) {
            PlotOrder initialOrder = new PlotOrder();
            initialOrder.setPlotId(plot.getId());
            initialOrder.setScenarioId(null);  // シナリオ連携なしで初期化
            initialOrder.setPlotOrder(0);
            initialOrder.setContent("");
            plotOrderRepository.save(initialOrder);
            orders = plotOrderRepository.findByPlotIdOrderByPlotOrderAsc(plot.getId());
        }

        // Viewに渡すデータ
        model.addAttribute("plot", plot);             // プロットタイトルなど
        model.addAttribute("plotOrders", orders);     // 各行のデータ

        return "plot/edit"; // Thymeleafテンプレート：resources/templates/plot/edit.html
    }

}



