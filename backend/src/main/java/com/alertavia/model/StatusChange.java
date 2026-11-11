package com.alertavia.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "status_changes")
public class StatusChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private ReportStatus newStatus;

    private LocalDateTime changedAt;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    protected StatusChange() {
    }

    public StatusChange(Report report, ReportStatus previousStatus, ReportStatus newStatus, String comment) {
        this.report = report;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.comment = comment;
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public ReportStatus getPreviousStatus() {
        return previousStatus;
    }

    public ReportStatus getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public String getComment() {
        return comment;
    }

    public Report getReport() {
        return report;
    }
}
