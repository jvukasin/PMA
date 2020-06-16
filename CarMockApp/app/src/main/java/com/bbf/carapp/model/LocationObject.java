package com.bbf.carapp.model;

import java.io.Serializable;

public class LocationObject implements Serializable {

    private double longitude;
    private double latitude;

    public LocationObject(){}

    public LocationObject(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
