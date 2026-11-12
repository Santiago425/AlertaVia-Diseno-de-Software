package com.alertavia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alertavia.dto.ReportResponse;
import com.alertavia.dto.StatusUpdateRequest;
import com.alertavia.model.Administrator;
import com.alertavia.model.Citizen;
import com.alertavia.model.Location;
import com.alertavia.model.MobilityEntity;
import com.alertavia.model.Report;
import com.alertavia.model.ReportStatus;
import com.alertavia.model.ReportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    private ReportService reportService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TrackingService trackingService;

    private Administrator admin;
    private Report report;

    @BeforeEach
    void setUp() {
        admin = new Administrator("Admin", "admin@alertavia.com", "secreto", "Coordinador");
        Citizen citizen = new Citizen("Laura Gómez", "laura@correo.com", "secreto", null);
        report = citizen.createReport("Semáforo apagado", ReportType.BROKEN_TRAFFIC_LIGHT,
                new Location(1.21, -77.28, "Calle 18 con Carrera 22"));
    }

    @Test
    void changesStatusAndNotifiesTheCitizen() {
        when(reportService.findReport(1L)).thenReturn(report);

        ReportResponse response = trackingService.changeStatus(1L, admin,
                new StatusUpdateRequest(ReportStatus.IN_REVIEW, "Reporte verificado"));

        assertThat(response.status()).isEqualTo(ReportStatus.IN_REVIEW);
        assertThat(response.history()).hasSize(2);
        verify(notificationService).notifyCitizen(eq(report), contains("En revisión"));
    }

    @Test
    void usesADefaultCommentWhenTheAdminDoesNotWriteOne() {
        when(reportService.findReport(1L)).thenReturn(report);

        ReportResponse response = trackingService.changeStatus(1L, admin,
                new StatusUpdateRequest(ReportStatus.REJECTED, "  "));

        assertThat(response.history().get(1).comment()).isEqualTo("Estado actualizado a Rechazado");
    }

    @Test
    void doesNotNotifyWhenTheTransitionIsInvalid() {
        when(reportService.findReport(1L)).thenReturn(report);

        assertThatThrownBy(() -> trackingService.changeStatus(1L, admin,
                new StatusUpdateRequest(ReportStatus.RESOLVED, null)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No se puede pasar de Reportado a Resuelto");
        verify(notificationService, never()).notifyCitizen(any(), any());
    }

    @Test
    void assignsTheMobilityEntityAndNotifiesTheCitizen() {
        MobilityEntity entity = new MobilityEntity("Empresa de Semaforización", "Semáforos");
        when(reportService.findReport(1L)).thenReturn(report);
        when(reportService.findMobilityEntity(2L)).thenReturn(entity);

        ReportResponse response = trackingService.assignEntity(1L, 2L, admin);

        assertThat(response.mobilityEntity()).isEqualTo("Empresa de Semaforización");
        assertThat(report.getReviewedBy()).isSameAs(admin);
        verify(notificationService).notifyCitizen(eq(report), contains("Empresa de Semaforización"));
    }
}
