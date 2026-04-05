package com.zorvyn.finance.controller;

import com.zorvyn.finance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
    @GetMapping("/trend")
public ResponseEntity<List<Map<String, Object>>> getTrend() {
    return ResponseEntity.ok(dashboardService.getTrend());
}
@GetMapping("/insights")
public ResponseEntity<Map<String, Object>> getInsights() {
    return ResponseEntity.ok(dashboardService.getInsights());
}
}