package com.alertavia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mobility_entities")
public class MobilityEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String zone;

    protected MobilityEntity() {
    }

    public MobilityEntity(String name, String zone) {
        this.name = name;
        this.zone = zone;
    }

    public void receiveReport(Report report) {
        report.assignTo(id, name);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getZone() {
        return zone;
    }
}
