package com.alertavia.service;

import com.alertavia.exception.ResourceNotFoundException;
import com.alertavia.integration.PushNotificationClient;
import com.alertavia.model.Notification;
import com.alertavia.model.Report;
import com.alertavia.repository.NotificationRepository;
import com.alertavia.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PushNotificationClient pushNotificationClient;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
                               PushNotificationClient pushNotificationClient) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.pushNotificationClient = pushNotificationClient;
    }

    public Notification notifyCitizen(Report report, String message) {
        Notification notification = notificationRepository.save(
                new Notification(report.getCitizenId(), report.getId(), message));
        userRepository.findById(report.getCitizenId()).ifPresent(citizen ->
                pushNotificationClient.send(citizen.getEmail(), "AlertaVía - Reporte #" + report.getCode(), message));
        return notification;
    }

    public List<Notification> getNotifications(String citizenId) {
        return notificationRepository.findByCitizenIdOrderBySentAtDesc(citizenId);
    }

    public long countUnread(String citizenId) {
        return notificationRepository.countByCitizenIdAndReadFalse(citizenId);
    }

    public Notification markAsRead(String notificationId, String citizenId) {
        Notification notification = notificationRepository.findById(notificationId)
                .filter(n -> n.getCitizenId().equals(citizenId))
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        notification.markAsRead();
        return notificationRepository.save(notification);
    }
}
