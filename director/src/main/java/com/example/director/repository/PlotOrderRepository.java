package com.example.director.repository;

import com.example.director.model.PlotOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlotOrderRepository extends JpaRepository<PlotOrder, Long> {
    List<PlotOrder> findByPlotIdOrderByPlotOrderAsc(Long plotId);
    void deleteByPlotId(Long plotId);
}
