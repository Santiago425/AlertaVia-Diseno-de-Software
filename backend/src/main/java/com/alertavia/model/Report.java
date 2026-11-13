package com.alertavia.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reports")
public class Report {

    @Id
    private String id;

    private String description;

    private LocalDateTime createdAt;

    @Indexed
    private ReportType type;

    @Indexed
    private ReportStatus status;

    private Location location;

    @Indexed
    private String citizenId;

    private String citizenName;

    private String reviewedById;

    private String mobilityEntityId;

    private String mobilityEntityName;

    private List<Photo> photos = new ArrayList<>();

    private List<StatusChange> history = new ArrayList<>();

    protected Report() {
    }

    Report(Citizen citizen, String description, ReportType type, Location location) {
        this.citizenId = citizen.getId();
        this.citizenName = citizen.getName();
        this.description = description;
        this.type = type;
        this.location = location;
        this.status = ReportStatus.REPORTED;
        this.createdAt = LocalDateTime.now();
        this.history.add(new StatusChange(null, ReportStatus.REPORTED, "Reporte creado por el ciudadano"));
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public StatusChange updateStatus(ReportStatus newStatus, String comment) {
        if (!status.canChangeTo(newStatus)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + status.label() + " a " + newStatus.label());
        }
        StatusChange change = new StatusChange(status, newStatus, comment);
        history.add(change);
        status = newStatus;
        return change;
    }

    public List<StatusChange> getHistory() {
        return history;
    }

    public String getCode() {
        if (id == null) {
            return "";
        }
        return id.substring(Math.max(0, id.length() - 6)).toUpperCase(Locale.ROOT);
    }

    void setReviewedById(String reviewedById) {
        this.reviewedById = reviewedById;
    }

    void assignTo(String entityId, String entityName) {
        this.mobilityEntityId = entityId;
        this.mobilityEntityName = entityName;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ReportType getType() {
        return type;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public Location getLocation() {
        return location;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public String getReviewedById() {
        return reviewedById;
    }

    public String getMobilityEntityId() {
        return mobilityEntityId;
    }

    public String getMobilityEntityName() {
        return mobilityEntityName;
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
