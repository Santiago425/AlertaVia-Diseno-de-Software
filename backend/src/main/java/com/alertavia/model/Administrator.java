package com.alertavia.model;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@TypeAlias("ADMIN")
public class Administrator extends User {

    private String position;

    protected Administrator() {
    }

    public Administrator(String name, String email, String password, String position) {
        super(name, email, password);
        this.position = position;
    }

    public void validateReport(Report report) {
        report.setReviewedById(getId());
        report.updateStatus(ReportStatus.IN_REVIEW, "Reporte validado por el administrador");
    }

    public StatusChange changeStatus(Report report, ReportStatus status, String comment) {
        report.setReviewedById(getId());
        return report.updateStatus(status, comment);
    }

    public void assignEntity(Report report, MobilityEntity entity) {
        report.setReviewedById(getId());
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
