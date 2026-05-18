package ar.com.baqueano.controller;

import ar.com.baqueano.dto.dashboard.DashboardResumenDTO;
import ar.com.baqueano.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDTO> resumen() {
        return ResponseEntity.ok(service.obtenerResumen());
    }
}
