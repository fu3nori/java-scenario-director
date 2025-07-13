package com.example.director.api;

import com.example.director.model.Plot;
import com.example.director.model.PlotOrder;
import com.example.director.model.User;
import com.example.director.repository.PlotOrderRepository;
import com.example.director.repository.PlotRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PlotApiController {

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private PlotOrderRepository plotOrderRepository;

    // ✅ タイトルのリアルタイム更新
    @PatchMapping("/plots/{id}")
    public void updatePlotTitle(@PathVariable Long id, @RequestBody Map<String, String> request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Plot plot = plotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "プロットが存在しません"));

        if (user == null || !user.getId().equals(plot.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "アクセス権限がありません");
        }

        plot.setTitle(request.get("title"));
        plotRepository.save(plot);
    }

    // ✅ プロット本文＋順番のリアルタイム更新
    @PatchMapping("/plot_orders/{id}")
    public void updatePlotOrder(@PathVariable Long id, @RequestBody Map<String, Object> request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        PlotOrder order = plotOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "プロット項目が存在しません"));

        Plot parentPlot = plotRepository.findById(order.getPlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "親プロットが存在しません"));

        if (user == null || !user.getId().equals(parentPlot.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "アクセス権限がありません");
        }

        order.setContent((String) request.get("content"));
        order.setPlotOrder((Integer) request.get("plotOrder"));
        plotOrderRepository.save(order);
    }

    // ✅ プロットの追加
    @PostMapping("/plot_orders")
    public PlotOrder createPlotOrder(@RequestParam Long plotId, @RequestParam int plotOrder, HttpSession session) {
        User user = (User) session.getAttribute("user");

        Plot parentPlot = plotRepository.findById(plotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "親プロットが存在しません"));

        if (user == null || !user.getId().equals(parentPlot.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "アクセス権限がありません");
        }

        PlotOrder newOrder = new PlotOrder();
        newOrder.setPlotId(plotId);
        newOrder.setScenarioId(null);
        newOrder.setPlotOrder(plotOrder);
        newOrder.setContent("");

        return plotOrderRepository.save(newOrder);
    }

    // ✅ プロットの削除
    @DeleteMapping("/plot_orders/{id}")
    public void deletePlotOrder(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("user");

        PlotOrder order = plotOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "プロット項目が存在しません"));

        Plot parentPlot = plotRepository.findById(order.getPlotId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "親プロットが存在しません"));

        if (user == null || !user.getId().equals(parentPlot.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "アクセス権限がありません");
        }

        plotOrderRepository.delete(order);
    }
}
