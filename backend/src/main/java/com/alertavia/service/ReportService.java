package com.alertavia.service;

import com.alertavia.dto.ReportRequest;
import com.alertavia.dto.ReportResponse;
import com.alertavia.exception.ResourceNotFoundException;
import com.alertavia.model.Citizen;
import com.alertavia.model.MobilityEntity;
import com.alertavia.model.Report;
import com.alertavia.model.ReportStatus;
import com.alertavia.model.ReportType;
import com.alertavia.model.User;
import com.alertavia.repository.ReportRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final LocationService locationService;
    private final PhotoStorageService photoStorageService;

    public ReportService(ReportRepository reportRepository, LocationService locationService,
                         PhotoStorageService photoStorageService) {
        this.reportRepository = reportRepository;
        this.locationService = locationService;
        this.photoStorageService = photoStorageService;
    }

    @Transactional
    public ReportResponse createReport(Citizen citizen, ReportRequest request, List<MultipartFile> photos) {
        Report report = citizen.createReport(
                request.description().trim(),
                request.type(),
                locationService.buildLocation(request.latitude(), request.longitude(), request.address()));
        photoStorageService.store(photos).forEach(report::addPhoto);
        return ReportResponse.from(reportRepository.save(report));
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getMyReports(Long citizenId) {
        return reportRepository.findByCitizenIdOrderByCreatedAtDesc(citizenId).stream()
                .map(ReportResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Long reportId, User user) {
        Report report = findReport(reportId);
        if (user instanceof Citizen && !report.getCitizen().getId().equals(user.getId())) {
            throw new AccessDeniedException("No puedes ver reportes de otros ciudadanos");
        }
        return ReportResponse.from(report);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports(ReportStatus status, ReportType type) {
        List<Report> reports = status == null
                ? reportRepository.findAllByOrderByCreatedAtDesc()
                : reportRepository.findByStatusOrderByCreatedAtDesc(status);
        return reports.stream()
                .filter(report -> type == null || report.getType() == type)
                .map(ReportResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ReportStatus status : ReportStatus.values()) {
            byStatus.put(status.name(), reportRepository.countByStatus(status));
        }
        Map<String, Long> byType = new LinkedHashMap<>();
        for (ReportType type : ReportType.values()) {
            byType.put(type.name(), reportRepository.countByType(type));
        }
        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("total", reportRepository.count());
        statistics.put("byStatus", byStatus);
        statistics.put("byType", byType);
        return statistics;
    }

    @Transactional(readOnly = true)
    public List<MobilityEntity> getMobilityEntities() {
        return reportRepository.findMobilityEntities();
    }

    @Transactional(readOnly = true)
    public Report findReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte " + reportId + " no encontrado"));
    }

    @Transactional(readOnly = true)
    public MobilityEntity findMobilityEntity(Long entityId) {
        return reportRepository.findMobilityEntityById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Entidad de movilidad no encontrada"));
    }
}
