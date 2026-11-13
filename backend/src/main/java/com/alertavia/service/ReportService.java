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
import com.alertavia.repository.MobilityEntityRepository;
import com.alertavia.repository.ReportRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final MobilityEntityRepository mobilityEntityRepository;
    private final LocationService locationService;
    private final PhotoStorageService photoStorageService;

    public ReportService(ReportRepository reportRepository, MobilityEntityRepository mobilityEntityRepository,
                         LocationService locationService, PhotoStorageService photoStorageService) {
        this.reportRepository = reportRepository;
        this.mobilityEntityRepository = mobilityEntityRepository;
        this.locationService = locationService;
        this.photoStorageService = photoStorageService;
    }

    public ReportResponse createReport(Citizen citizen, ReportRequest request, List<MultipartFile> photos) {
        Report report = citizen.createReport(
                request.description().trim(),
                request.type(),
                locationService.buildLocation(request.latitude(), request.longitude(), request.address()));
        photoStorageService.store(photos).forEach(report::addPhoto);
        return ReportResponse.from(reportRepository.save(report));
    }

    public List<ReportResponse> getMyReports(String citizenId) {
        return reportRepository.findByCitizenIdOrderByCreatedAtDesc(citizenId).stream()
                .map(ReportResponse::from)
                .toList();
    }

    public ReportResponse getReport(String reportId, User user) {
        Report report = findReport(reportId);
        if (user instanceof Citizen && !report.getCitizenId().equals(user.getId())) {
            throw new AccessDeniedException("No puedes ver reportes de otros ciudadanos");
        }
        return ReportResponse.from(report);
    }

    public List<ReportResponse> getAllReports(ReportStatus status, ReportType type) {
        List<Report> reports = status == null
                ? reportRepository.findAllByOrderByCreatedAtDesc()
                : reportRepository.findByStatusOrderByCreatedAtDesc(status);
        return reports.stream()
                .filter(report -> type == null || report.getType() == type)
                .map(ReportResponse::from)
                .toList();
    }

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

    public List<MobilityEntity> getMobilityEntities() {
        return mobilityEntityRepository.findAllByOrderByNameAsc();
    }

    public Report findReport(String reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado"));
    }

    public MobilityEntity findMobilityEntity(String entityId) {
        return mobilityEntityRepository.findById(entityId)
                .orElseThrow(() -> new ResourceNotFoundException("Entidad de movilidad no encontrada"));
    }

    public Report save(Report report) {
        return reportRepository.save(report);
    }
}
