package com.alertavia.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ReportTest {

    private Citizen citizen;
    private Administrator admin;
    private Report report;

    @BeforeEach
    void setUp() {
        citizen = new Citizen("Ana Pérez", "ana@correo.com", "secreto", "3001234567");
        admin = new Administrator("Admin", "admin@correo.com", "secreto", "Coordinador");
        ReflectionTestUtils.setField(citizen, "id", "c-001");
        ReflectionTestUtils.setField(admin, "id", "a-001");
        report = citizen.createReport("Hueco grande en la vía", ReportType.POTHOLE,
                new Location(1.2136, -77.2811, "Calle 18 # 25-10, Pasto"));
    }

    @Test
    void newReportStartsAsReportedWithHistory() {
        assertThat(report.getStatus()).isEqualTo(ReportStatus.REPORTED);
        assertThat(report.getHistory()).hasSize(1);
        assertThat(report.getCitizenId()).isEqualTo("c-001");
        assertThat(report.getCitizenName()).isEqualTo("Ana Pérez");
    }

    @Test
    void administratorFollowsTheStatusFlow() {
        admin.validateReport(report);
        admin.changeStatus(report, ReportStatus.IN_PROGRESS, "Cuadrilla asignada");
        admin.changeStatus(report, ReportStatus.RESOLVED, "Hueco reparado");

        assertThat(report.getStatus()).isEqualTo(ReportStatus.RESOLVED);
        assertThat(report.getHistory()).extracting(StatusChange::getNewStatus).containsExactly(
                ReportStatus.REPORTED, ReportStatus.IN_REVIEW, ReportStatus.IN_PROGRESS, ReportStatus.RESOLVED);
        assertThat(report.getReviewedById()).isEqualTo("a-001");
    }

    @Test
    void cannotSkipStatuses() {
        assertThatThrownBy(() -> admin.changeStatus(report, ReportStatus.RESOLVED, "Listo"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void mobilityEntityReceivesReport() {
        MobilityEntity entity = new MobilityEntity("Secretaría de Infraestructura", "Vías");
        ReflectionTestUtils.setField(entity, "id", "m-001");
        admin.assignEntity(report, entity);
        assertThat(report.getMobilityEntityId()).isEqualTo("m-001");
        assertThat(report.getMobilityEntityName()).isEqualTo("Secretaría de Infraestructura");
    }

    @Test
    void locationBuildsGoogleMapsUrl() {
        assertThat(report.getLocation().getMapUrl()).contains("1.2136,-77.2811");
    }

    @Test
    void codeUsesTheLastCharactersOfTheId() {
        ReflectionTestUtils.setField(report, "id", "6730f1c2a9e4b5d8c1f2a3b4");
        assertThat(report.getCode()).isEqualTo("F2A3B4");
    }

    @Test
    void notificationCanBeMarkedAsRead() {
        Notification notification = new Notification("c-001", "r-001", "Tu reporte cambió de estado");
        notification.markAsRead();
        assertThat(notification.isRead()).isTrue();
    }
}
