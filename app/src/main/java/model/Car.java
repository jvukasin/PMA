package model;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Car implements Serializable {

    private String brand;
    private String model;
    private int avatar;
    private String reg_number;
    private double mileage;
    private int fuel_distance;
    private int no_of_rides;
    private double rating;
    private float tp_fl;
    private float tp_fr;
    private float tp_rl;
    private float tp_rr;
    private LocationObject location;

    public Car(){

    }

    public Car(String brand, String model, int avatar, String reg_number, double mileage, int fuel_distance, int no_of_rides, double rating, float tp_fl, float tp_fr, float tp_rl, float tp_rr, LocationObject location) {
        this.brand = brand;
        this.model = model;
        this.avatar = avatar;
        this.reg_number = reg_number;
        this.mileage = mileage;
        this.fuel_distance = fuel_distance;
        this.no_of_rides = no_of_rides;
        this.rating = rating;
        this.tp_fl = tp_fl;
        this.tp_fr = tp_fr;
        this.tp_rl = tp_rl;
        this.tp_rr = tp_rr;
        this.location = location;
    }

    public LocationObject getLocation() {
        return location;
    }

    public void setLocation(LocationObject location) {
        this.location = location;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getReg_number() {
        return reg_number;
    }

    public void setReg_number(String reg_number) {
        this.reg_number = reg_number;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public int getFuel_distance() {
        return fuel_distance;
    }

    public void setFuel_distance(int fuel_distance) {
        this.fuel_distance = fuel_distance;
    }

    public int getNo_of_rides() {
        return no_of_rides;
    }

    public void setNo_of_rides(int no_of_rides) {
        this.no_of_rides = no_of_rides;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public float getTp_fl() {
        return tp_fl;
    }

    public void setTp_fl(float tp_fl) {
        this.tp_fl = tp_fl;
    }

    public float getTp_fr() {
        return tp_fr;
    }

    public void setTp_fr(float tp_fr) {
        this.tp_fr = tp_fr;
    }

    public float getTp_rl() {
        return tp_rl;
    }

    public void setTp_rl(float tp_rl) {
        this.tp_rl = tp_rl;
    }

    public float getTp_rr() {
        return tp_rr;
    }

    public void setTp_rr(float tp_rr) {
        this.tp_rr = tp_rr;
    }
}
