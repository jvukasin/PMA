package com.bbf.carapp.model;

public class Rent {

    private String plates;
    private LocationObject location;
    private String active;

    public Rent() {
    }

    public Rent(String plates, LocationObject location, String active) {
        this.plates = plates;
        this.location = location;
        this.active = active;
    }

    public String getPlates() {
        return plates;
    }

    public void setPlates(String plates) {
        this.plates = plates;
    }

    public LocationObject getLocation() {
        return location;
    }

    public void setLocation(LocationObject location) {
        this.location = location;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }
}
