package com.alertavia.dto;

import com.alertavia.model.Photo;
import com.alertavia.model.Report;
import com.alertavia.model.ReportStatus;
import com.alertavia.model.ReportType;
import com.alertavia.model.StatusChange;
import java.time.LocalDateTime;
import java.util.List;

public record ReportResponse(
        String id,
        String code,
        String description,
        ReportType type,
        ReportStatus status,
        String statusLabel,
        double latitude,
        double longitude,
        String address,
        String mapUrl,
        LocalDateTime createdAt,
        String citizenName,
        String mobilityEntity,
        List<String> photos,
        List<HistoryEntry> history) {

    public record HistoryEntry(ReportStatus previousStatus, ReportStatus newStatus, String comment,
                               LocalDateTime changedAt) {

        static HistoryEntry from(StatusChange change) {
            return new HistoryEntry(change.getPreviousStatus(), change.getNewStatus(), change.getComment(),
                    change.getChangedAt());
        }
    }

    public static ReportResponse from(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getCode(),
                report.getDescription(),
                report.getType(),
                report.getStatus(),
                report.getStatus().label(),
                report.getLocation().getLatitude(),
                report.getLocation().getLongitude(),
                report.getLocation().getAddress(),
                report.getLocation().getMapUrl(),
                report.getCreatedAt(),
                report.getCitizenName(),
                report.getMobilityEntityName(),
                report.getPhotos().stream().map(Photo::getUrl).toList(),
                report.getHistory().stream().map(HistoryEntry::from).toList());
    }
}
