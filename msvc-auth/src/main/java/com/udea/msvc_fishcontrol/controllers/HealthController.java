package com.udea.msvc_fishcontrol.controllers;

import com.udea.msvc_fishcontrol.response.DatabaseStatusResponse;
import com.udea.msvc_fishcontrol.service.DatabaseHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DatabaseHealthService databaseHealthService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "msvc-auth");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/db")
    public ResponseEntity<DatabaseStatusResponse> databaseHealthCheck() {
        return ResponseEntity.ok(databaseHealthService.checkDatabaseConnections());
    }
}