package com.emergency.incident;

import java.util.Date;

public class Incident {
    private String incidentType;
    private String description;
    private double latitude;
    private double longitude;
    private String severity;
    private String imageName; // Tracks the photo reference file name
    private Date timestamp;

    public Incident(String incidentType, String description, double latitude, double longitude, String severity, String imageName) {
        this.incidentType = incidentType;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.severity = severity;
        this.imageName = imageName;
        this.timestamp = new Date();
    }

    // Getters
    public String getIncidentType() { return incidentType; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getSeverity() { return severity; }
    public String getImageName() { return imageName; }
    public Date getTimestamp() { return timestamp; }
}