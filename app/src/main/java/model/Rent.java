package model;

import java.io.Serializable;
import java.util.Date;

public class Rent implements Serializable {

    private String id;
    private Date date_created;
    private Date date_ended;
    private double distance;
    private String car_reg_number;
    private String user_id;

    public Rent() {
    }

    public Rent(String id, Date date_created, Date date_ended, double distance, String car_reg_number, String user_id) {
        this.id = id;
        this.date_created = date_created;
        this.date_ended = date_ended;
        this.distance = distance;
        this.car_reg_number = car_reg_number;
        this.user_id = user_id;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public Date getDate_ended() {
        return date_ended;
    }

    public void setDate_ended(Date date_ended) {
        this.date_ended = date_ended;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCar_reg_number() {
        return car_reg_number;
    }

    public void setCar_reg_number(String car_reg_number) {
        this.car_reg_number = car_reg_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
