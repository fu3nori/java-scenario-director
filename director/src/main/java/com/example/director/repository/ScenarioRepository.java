package com.example.director.repository;

import com.example.director.model.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
    List<Scenario> findByUserId(Long userId);
    Page<Scenario> findByUserId(Long userId, Pageable pageable);
}
