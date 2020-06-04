package model;

import android.os.Parcel;
import android.os.Parcelable;

public class CarItem implements Parcelable {

    private String brand;
    private String model;
    private int avatar;
    private String reg_number;
    private double milage;
    private int fuel_distance;
    private int no_of_rides;
    private double rating;
    private float tp_fl;
    private float tp_fr;
    private float tp_rl;
    private float tp_rr;
    private double distanceFromMe;

    public CarItem(Car car){
        this(car.getBrand(), car.getModel(), car.getAvatar(), car.getReg_number(), car.getMileage(), car.getFuel_distance(), car.getNo_of_rides(), car.getRating(),
                car.getTp_fl(), car.getTp_fr(), car.getTp_rl(), car.getTp_rr(), 0);
    }

    protected CarItem(Parcel in) {
        brand = in.readString();
        model = in.readString();
        avatar = in.readInt();
        reg_number = in.readString();
        milage = in.readDouble();
        fuel_distance = in.readInt();
        no_of_rides = in.readInt();
        rating = in.readDouble();
        tp_fl = in.readFloat();
        tp_fr = in.readFloat();
        tp_rl = in.readFloat();
        tp_rr = in.readFloat();
        distanceFromMe = in.readDouble();
    }

    public CarItem(String brand, String model, int avatar, String reg_number, double milage, int fuel_distance, int no_of_rides, double rating, float tp_fl, float tp_fr, float tp_rl, float tp_rr, double distanceFromMe) {
        this.brand = brand;
        this.model = model;
        this.avatar = avatar;
        this.reg_number = reg_number;
        this.milage = milage;
        this.fuel_distance = fuel_distance;
        this.no_of_rides = no_of_rides;
        this.rating = rating;
        this.tp_fl = tp_fl;
        this.tp_fr = tp_fr;
        this.tp_rl = tp_rl;
        this.tp_rr = tp_rr;
        this.distanceFromMe = distanceFromMe;
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

    public double getDistanceFromMe() {
        return distanceFromMe;
    }

    public void setDistanceFromMe(double distanceFromMe) {
        this.distanceFromMe = distanceFromMe;
    }

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

    public double getMilage() {
        return milage;
    }

    public void setMilage(double milage) {
        this.milage = milage;
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
        dest.writeDouble(milage);
        dest.writeInt(fuel_distance);
        dest.writeInt(no_of_rides);
        dest.writeDouble(rating);
        dest.writeFloat(tp_fl);
        dest.writeFloat(tp_rr);
        dest.writeFloat(tp_rl);
        dest.writeFloat(tp_rr);
        dest.writeDouble(distanceFromMe);
    }
}
