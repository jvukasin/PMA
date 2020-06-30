package com.bbf.cruise.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bbf.cruise.Mokap;
import com.bbf.cruise.R;
import com.bbf.cruise.tools.NetworkUtil;

import org.w3c.dom.Text;

import model.RideHistoryItem;

//TODO: dodati prave podatke iz baze
public class RideHistoryAdapter extends BaseAdapter {

    private Activity activity;

    public RideHistoryAdapter(Activity activity) { this.activity = activity; }

    @Override
    public int getCount() {
        return Mokap.getRideHistoryItems().size();
    }

    @Override
    public Object getItem(int position) {
        return Mokap.getRideHistoryItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        RideHistoryItem rideHistoryItem = Mokap.getRideHistoryItems().get(position);

        if(convertView == null){
            vi = activity.getLayoutInflater().inflate(R.layout.ride_history_item, null);
        }

        TextView startDate = (TextView) vi.findViewById(R.id.startDate);
        TextView endDate = (TextView) vi.findViewById(R.id.endDate);
        TextView distance = (TextView) vi.findViewById(R.id.distance);
        TextView price = (TextView) vi.findViewById(R.id.price);
        TextView points = (TextView) vi.findViewById(R.id.points);

        price.setText(String.valueOf(rideHistoryItem.getPrice()));
        startDate.setText(rideHistoryItem.getStartDate());
        endDate.setText(rideHistoryItem.getEndDate());
        distance.setText(String.valueOf(rideHistoryItem.getDistance()));
        points.setText(String.valueOf(rideHistoryItem.getPoints()));

        return vi;
    }

}
