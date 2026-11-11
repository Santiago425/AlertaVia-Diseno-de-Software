package com.alertavia.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Location {

    private double latitude;
    private double longitude;
    private String address;

    protected Location() {
    }

    public Location(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public String getMapUrl() {
        return "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
