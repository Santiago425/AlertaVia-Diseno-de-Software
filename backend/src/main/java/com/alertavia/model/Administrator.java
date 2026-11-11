package com.alertavia.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Administrator extends User {

    private String position;

    protected Administrator() {
    }

    public Administrator(String name, String email, String password, String position) {
        super(name, email, password);
        this.position = position;
    }

    public void validateReport(Report report) {
        report.setReviewedBy(this);
        report.updateStatus(ReportStatus.IN_REVIEW, "Reporte validado por el administrador");
    }

    public StatusChange changeStatus(Report report, ReportStatus status, String comment) {
        report.setReviewedBy(this);
        return report.updateStatus(status, comment);
    }

    public void assignEntity(Report report, MobilityEntity entity) {
        report.setReviewedBy(this);
        entity.receiveReport(report);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
