package com.alertavia.service;

import com.alertavia.exception.ResourceNotFoundException;
import com.alertavia.integration.PushNotificationClient;
import com.alertavia.model.Citizen;
import com.alertavia.model.Notification;
import com.alertavia.model.Report;
import com.alertavia.repository.NotificationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PushNotificationClient pushNotificationClient;

    public NotificationService(NotificationRepository notificationRepository,
                               PushNotificationClient pushNotificationClient) {
        this.notificationRepository = notificationRepository;
        this.pushNotificationClient = pushNotificationClient;
    }

    @Transactional
    public Notification notifyCitizen(Report report, String message) {
        Citizen citizen = report.getCitizen();
        Notification notification = notificationRepository.save(new Notification(citizen, report.getId(), message));
        pushNotificationClient.send(citizen.getEmail(), "AlertaVía - Reporte #" + report.getId(), message);
        return notification;
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(Long citizenId) {
        return notificationRepository.findByCitizenIdOrderBySentAtDesc(citizenId);
    }

    @Transactional(readOnly = true)
    public long countUnread(Long citizenId) {
        return notificationRepository.countByCitizenIdAndReadFalse(citizenId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId, Long citizenId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getCitizen().getId().equals(citizenId))
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        notification.markAsRead();
        return notification;
    }
}
