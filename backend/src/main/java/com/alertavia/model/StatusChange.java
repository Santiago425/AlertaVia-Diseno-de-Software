package com.alertavia.model;

import java.time.LocalDateTime;

public class StatusChange {

    private ReportStatus previousStatus;
    private ReportStatus newStatus;
    private LocalDateTime changedAt;
    private String comment;

    protected StatusChange() {
    }

    public StatusChange(ReportStatus previousStatus, ReportStatus newStatus, String comment) {
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.comment = comment;
        this.changedAt = LocalDateTime.now();
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
}
