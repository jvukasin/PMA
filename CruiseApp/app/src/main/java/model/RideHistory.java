package model;

import java.io.Serializable;

public class RideHistory implements Serializable {

    private String startDate;
    private String endDate;
    private double distance;
    private double price;
    private int points;
    private String userId;
    private String image;

    public RideHistory(){}

    public RideHistory(String startDate, String endDate, double distance, double price, int points, String userId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.distance = distance;
        this.price = price;
        this.points = points;
        this.userId = userId;
    }

    public RideHistory(RideHistory rideHistory) {
        this(rideHistory.getStartDate(), rideHistory.getEndDate(), rideHistory.getDistance(), rideHistory.getPrice(), rideHistory.getPoints(), rideHistory.getUserId());
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
