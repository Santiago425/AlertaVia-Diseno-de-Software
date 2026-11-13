package com.alertavia.controller;

import com.alertavia.model.Notification;
import com.alertavia.service.AuthService;
import com.alertavia.service.NotificationService;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    public NotificationController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @GetMapping
    public List<Notification> list(Authentication authentication) {
        return notificationService.getNotifications(citizenId(authentication));
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Authentication authentication) {
        return Map.of("count", notificationService.countUnread(citizenId(authentication)));
    }

    @PatchMapping("/{id}/read")
    public Notification markAsRead(@PathVariable String id, Authentication authentication) {
        return notificationService.markAsRead(id, citizenId(authentication));
    }

    private String citizenId(Authentication authentication) {
        return authService.findCitizen(authentication.getName()).getId();
    }
}
