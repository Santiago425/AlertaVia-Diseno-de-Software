package com.alertavia.model;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@TypeAlias("CITIZEN")
public class Citizen extends User {

    private String phone;

    protected Citizen() {
    }

    public Citizen(String name, String email, String password, String phone) {
        super(name, email, password);
        this.phone = phone;
    }

    public Report createReport(String description, ReportType type, Location location) {
        return new Report(this, description, type, location);
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
}
