package com.alertavia.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String description;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Embedded
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Administrator reviewedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mobility_entity_id")
    private MobilityEntity mobilityEntity;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    private List<StatusChange> history = new ArrayList<>();

    protected Report() {
    }

    Report(Citizen citizen, String description, ReportType type, Location location) {
        this.citizen = citizen;
        this.description = description;
        this.type = type;
        this.location = location;
        this.status = ReportStatus.REPORTED;
        this.createdAt = LocalDateTime.now();
        this.history.add(new StatusChange(this, null, ReportStatus.REPORTED, "Reporte creado por el ciudadano"));
    }

    public void addPhoto(Photo photo) {
        photo.setReport(this);
        photos.add(photo);
    }

    public StatusChange updateStatus(ReportStatus newStatus, String comment) {
        if (!status.canChangeTo(newStatus)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + status.label() + " a " + newStatus.label());
        }
        StatusChange change = new StatusChange(this, status, newStatus, comment);
        history.add(change);
        status = newStatus;
        return change;
    }

    public List<StatusChange> getHistory() {
        return history;
    }

    public Long getId() {
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

    public Citizen getCitizen() {
        return citizen;
    }

    public Administrator getReviewedBy() {
        return reviewedBy;
    }

    void setReviewedBy(Administrator reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public MobilityEntity getMobilityEntity() {
        return mobilityEntity;
    }

    void setMobilityEntity(MobilityEntity mobilityEntity) {
        this.mobilityEntity = mobilityEntity;
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
