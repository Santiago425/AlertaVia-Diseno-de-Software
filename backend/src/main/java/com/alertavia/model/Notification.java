package com.alertavia.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    private String message;

    private LocalDateTime sentAt;

    private boolean read;

    private String reportId;

    @Indexed
    private String citizenId;

    protected Notification() {
    }

    public Notification(String citizenId, String reportId, String message) {
        this.citizenId = citizenId;
        this.reportId = reportId;
        this.message = message;
        this.sentAt = LocalDateTime.now();
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public String getReportId() {
        return reportId;
    }

    public String getCitizenId() {
        return citizenId;
    }
}
