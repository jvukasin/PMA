package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class RideHistoryItem implements Parcelable {

    private String startDate;
    private String endDate;
    private double distance;
    private double price;
    private int points;

    public RideHistoryItem(RideHistory rideHistory){
        this(rideHistory.getStartDate(), rideHistory.getEndDate(), rideHistory.getDistance(), rideHistory.getPrice(), rideHistory.getPoints());
    }

    public RideHistoryItem(String startDate, String endDate, double distance, double price, int points) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.distance = distance;
        this.price = price;
        this.points = points;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeDouble(distance);
        dest.writeDouble(price);
    }
}
