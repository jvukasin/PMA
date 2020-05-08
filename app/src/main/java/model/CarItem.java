package model;

import android.os.Parcel;
import android.os.Parcelable;

public class CarItem implements Parcelable {

    private String brand;
    private String model;
    private int avatar;
    private String reg_number;
    private double distance;
    private int fuel_distance;
    private int no_of_rides;
    private double rating;

    public CarItem(String brand, String model, int avatar, String reg_number, double distance, int fuel_distance, int no_of_rides, double rating) {
        this.brand = brand;
        this.model = model;
        this.avatar = avatar;
        this.reg_number = reg_number;
        this.distance = distance;
        this.fuel_distance = fuel_distance;
        this.no_of_rides = no_of_rides;
        this.rating = rating;
    }

    protected CarItem(Parcel in) {
        brand = in.readString();
        model = in.readString();
        avatar = in.readInt();
        reg_number = in.readString();
        distance = in.readDouble();
        fuel_distance = in.readInt();
        no_of_rides = in.readInt();
        rating = in.readDouble();
    }

    public static final Creator<CarItem> CREATOR = new Creator<CarItem>() {
        @Override
        public CarItem createFromParcel(Parcel in) {
            return new CarItem(in);
        }

        @Override
        public CarItem[] newArray(int size) {
            return new CarItem[size];
        }
    };

    public String getCarName() {
        return brand + " " + model;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getFuel_distance() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(brand + " " + model);
        dest.writeString(reg_number);
        dest.writeDouble(distance);
        dest.writeInt(fuel_distance);
        dest.writeInt(no_of_rides);
        dest.writeDouble(rating);
    }
}
