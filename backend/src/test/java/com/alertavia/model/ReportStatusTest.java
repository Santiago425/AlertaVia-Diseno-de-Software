package com.alertavia.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ReportStatusTest {

    @ParameterizedTest
    @CsvSource({
            "REPORTED, IN_REVIEW, true",
            "REPORTED, REJECTED, true",
            "REPORTED, IN_PROGRESS, false",
            "REPORTED, RESOLVED, false",
            "IN_REVIEW, IN_PROGRESS, true",
            "IN_REVIEW, REJECTED, true",
            "IN_REVIEW, RESOLVED, false",
            "IN_PROGRESS, RESOLVED, true",
            "IN_PROGRESS, REJECTED, false",
            "RESOLVED, REPORTED, false",
            "REJECTED, IN_REVIEW, false"
    })
    void validatesStatusTransitions(ReportStatus current, ReportStatus next, boolean allowed) {
        assertThat(current.canChangeTo(next)).isEqualTo(allowed);
    }

    @ParameterizedTest
    @CsvSource({
            "REPORTED, Reportado",
            "IN_REVIEW, En revisión",
            "IN_PROGRESS, En proceso",
            "RESOLVED, Resuelto",
            "REJECTED, Rechazado"
    })
    void hasSpanishLabels(ReportStatus status, String label) {
        assertThat(status.label()).isEqualTo(label);
    }
}
