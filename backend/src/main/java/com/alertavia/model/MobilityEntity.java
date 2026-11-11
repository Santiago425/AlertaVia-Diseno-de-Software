package com.alertavia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mobility_entities")
public class MobilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String zone;

    protected MobilityEntity() {
    }

    public MobilityEntity(String name, String zone) {
        this.name = name;
        this.zone = zone;
    }

    public void receiveReport(Report report) {
        report.setMobilityEntity(this);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZone() {
        return zone;
    }
}
