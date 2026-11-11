package com.alertavia.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportTest {

    private Citizen citizen;
    private Administrator admin;
    private Report report;

    @BeforeEach
    void setUp() {
        citizen = new Citizen("Ana Pérez", "ana@correo.com", "secreto", "3001234567");
        admin = new Administrator("Admin", "admin@correo.com", "secreto", "Coordinador");
        report = citizen.createReport("Hueco grande en la vía", ReportType.POTHOLE,
                new Location(1.2136, -77.2811, "Calle 18 # 25-10, Pasto"));
    }

    @Test
    void newReportStartsAsReportedWithHistory() {
        assertThat(report.getStatus()).isEqualTo(ReportStatus.REPORTED);
        assertThat(report.getHistory()).hasSize(1);
        assertThat(report.getCitizen()).isSameAs(citizen);
    }

    @Test
    void administratorFollowsTheStatusFlow() {
        admin.validateReport(report);
        admin.changeStatus(report, ReportStatus.IN_PROGRESS, "Cuadrilla asignada");
        admin.changeStatus(report, ReportStatus.RESOLVED, "Hueco reparado");

        assertThat(report.getStatus()).isEqualTo(ReportStatus.RESOLVED);
        assertThat(report.getHistory()).extracting(StatusChange::getNewStatus).containsExactly(
                ReportStatus.REPORTED, ReportStatus.IN_REVIEW, ReportStatus.IN_PROGRESS, ReportStatus.RESOLVED);
        assertThat(report.getReviewedBy()).isSameAs(admin);
    }

    @Test
    void cannotSkipStatuses() {
        assertThatThrownBy(() -> admin.changeStatus(report, ReportStatus.RESOLVED, "Listo"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void mobilityEntityReceivesReport() {
        MobilityEntity entity = new MobilityEntity("Secretaría de Infraestructura", "Vías");
        admin.assignEntity(report, entity);
        assertThat(report.getMobilityEntity()).isSameAs(entity);
    }

    @Test
    void locationBuildsGoogleMapsUrl() {
        assertThat(report.getLocation().getMapUrl()).contains("1.2136,-77.2811");
    }

    @Test
    void notificationCanBeMarkedAsRead() {
        Notification notification = new Notification(citizen, 1L, "Tu reporte cambió de estado");
        notification.markAsRead();
        assertThat(notification.isRead()).isTrue();
    }
}
