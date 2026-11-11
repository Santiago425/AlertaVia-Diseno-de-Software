package com.alertavia.model;

import java.util.EnumSet;
import java.util.Set;

public enum ReportStatus {
    REPORTED,
    IN_REVIEW,
    IN_PROGRESS,
    RESOLVED,
    REJECTED;

    public Set<ReportStatus> nextStatuses() {
        return switch (this) {
            case REPORTED -> EnumSet.of(IN_REVIEW, REJECTED);
            case IN_REVIEW -> EnumSet.of(IN_PROGRESS, REJECTED);
            case IN_PROGRESS -> EnumSet.of(RESOLVED);
            case RESOLVED, REJECTED -> EnumSet.noneOf(ReportStatus.class);
        };
    }

    public boolean canChangeTo(ReportStatus next) {
        return nextStatuses().contains(next);
    }

    public String label() {
        return switch (this) {
            case REPORTED -> "Reportado";
            case IN_REVIEW -> "En revisión";
            case IN_PROGRESS -> "En proceso";
            case RESOLVED -> "Resuelto";
            case REJECTED -> "Rechazado";
        };
    }
}
