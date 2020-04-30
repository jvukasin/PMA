package com.bbf.cruise;

import java.util.ArrayList;
import java.util.List;

import model.RideHistoryItem;

public class Mokap {

    public static List<RideHistoryItem> getRideHistoryItems(){
        ArrayList<RideHistoryItem> rideHistoryItems = new ArrayList<RideHistoryItem>();
        RideHistoryItem item1 = new RideHistoryItem("10.10.2020. 15:30", "10.10.2020. 15:58", 10, 500, 1000);
        RideHistoryItem item2 = new RideHistoryItem("10.10.2020. 15:30", "10.10.2020. 15:58", 10, 500, 1000);
        RideHistoryItem item3 = new RideHistoryItem("10.10.2020. 15:30", "10.10.2020. 15:58", 10, 500, 1000);

        rideHistoryItems.add(item1);
        rideHistoryItems.add(item2);
        rideHistoryItems.add(item3);

        return rideHistoryItems;
    }

}
