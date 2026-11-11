package com.alertavia.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("CITIZEN")
public class Citizen extends User {

    private String phone;

    @OneToMany(mappedBy = "citizen")
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "citizen")
    private List<Notification> notifications = new ArrayList<>();

    protected Citizen() {
    }

    public Citizen(String name, String email, String password, String phone) {
        super(name, email, password);
        this.phone = phone;
    }

    public Report createReport(String description, ReportType type, Location location) {
        return new Report(this, description, type, location);
    }

    public List<Report> getMyReports() {
        return reports;
    }

    @Override
    public String getRole() {
        return "CITIZEN";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}
