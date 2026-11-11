package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.DashboardDto;
import br.com.darkkitchen.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public DashboardDto.Resposta resumo() {
        return dashboardService.resumo();
    }
}
