package com.alertavia.controller;

import com.alertavia.dto.ReportResponse;
import com.alertavia.dto.StatusUpdateRequest;
import com.alertavia.model.Administrator;
import com.alertavia.model.MobilityEntity;
import com.alertavia.model.ReportStatus;
import com.alertavia.model.ReportType;
import com.alertavia.service.AuthService;
import com.alertavia.service.ReportService;
import com.alertavia.service.TrackingService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ReportService reportService;
    private final TrackingService trackingService;
    private final AuthService authService;

    public AdminController(ReportService reportService, TrackingService trackingService, AuthService authService) {
        this.reportService = reportService;
        this.trackingService = trackingService;
        this.authService = authService;
    }

    @GetMapping("/reports")
    public List<ReportResponse> reports(@RequestParam(required = false) ReportStatus status,
                                        @RequestParam(required = false) ReportType type) {
        return reportService.getAllReports(status, type);
    }

    @PutMapping("/reports/{id}/status")
    public ReportResponse changeStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request,
                                       Authentication authentication) {
        Administrator admin = authService.findAdministrator(authentication.getName());
        return trackingService.changeStatus(id, admin, request);
    }

    @PutMapping("/reports/{id}/entity/{entityId}")
    public ReportResponse assignEntity(@PathVariable String id, @PathVariable String entityId,
                                       Authentication authentication) {
        Administrator admin = authService.findAdministrator(authentication.getName());
        return trackingService.assignEntity(id, entityId, admin);
    }

    @GetMapping("/entities")
    public List<MobilityEntity> entities() {
        return reportService.getMobilityEntities();
    }

    @GetMapping("/statistics")
    public Map<String, Object> statistics() {
        return reportService.getStatistics();
    }
}
