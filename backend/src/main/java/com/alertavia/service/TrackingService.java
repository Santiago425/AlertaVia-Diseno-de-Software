package com.alertavia.service;

import com.alertavia.dto.ReportResponse;
import com.alertavia.dto.StatusUpdateRequest;
import com.alertavia.model.Administrator;
import com.alertavia.model.MobilityEntity;
import com.alertavia.model.Report;
import com.alertavia.model.StatusChange;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TrackingService {

    private final ReportService reportService;
    private final NotificationService notificationService;

    public TrackingService(ReportService reportService, NotificationService notificationService) {
        this.reportService = reportService;
        this.notificationService = notificationService;
    }

    @Transactional
    public ReportResponse changeStatus(Long reportId, Administrator admin, StatusUpdateRequest request) {
        Report report = reportService.findReport(reportId);
        String comment = StringUtils.hasText(request.comment())
                ? request.comment().trim()
                : "Estado actualizado a " + request.status().label();
        StatusChange change = admin.changeStatus(report, request.status(), comment);
        notificationService.notifyCitizen(report, "Tu reporte #" + report.getId() + " pasó a "
                + change.getNewStatus().label() + ". " + comment);
        return ReportResponse.from(report);
    }

    @Transactional
    public ReportResponse assignEntity(Long reportId, Long entityId, Administrator admin) {
        Report report = reportService.findReport(reportId);
        MobilityEntity entity = reportService.findMobilityEntity(entityId);
        admin.assignEntity(report, entity);
        notificationService.notifyCitizen(report, "Tu reporte #" + report.getId() + " fue asignado a "
                + entity.getName() + ".");
        return ReportResponse.from(report);
    }
}
