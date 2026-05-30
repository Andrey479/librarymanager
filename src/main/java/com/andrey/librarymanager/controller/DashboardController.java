package com.andrey.librarymanager.controller;

import com.andrey.librarymanager.dto.DashboardResponseDTO;
import com.andrey.librarymanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String getDashboard(Model model){
        DashboardResponseDTO dashboard = dashboardService.getDashboard();

        model.addAttribute("dashboard", dashboard);

        return "dashboard";
    }
}
