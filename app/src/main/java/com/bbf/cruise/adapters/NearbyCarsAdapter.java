package com.bbf.cruise.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbf.cruise.R;

import java.util.ArrayList;

import model.CarItem;

public class NearbyCarsAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<CarItem> list;

    public NearbyCarsAdapter(Activity act, ArrayList<CarItem> list) {
        this.activity = act;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if(convertView == null){
            vi = activity.getLayoutInflater().inflate(R.layout.nearby_cars_item, null);
        }

        TextView name = (TextView) vi.findViewById(R.id.carItemName);
        ImageView icon = (ImageView) vi.findViewById(R.id.carItemIcon);
        TextView plate = (TextView) vi.findViewById(R.id.carItemRegPlate);
        TextView fuel = (TextView) vi.findViewById(R.id.carItemFuelDis);
        TextView distance = (TextView) vi.findViewById(R.id.carItemFromMeDis);

        name.setText(list.get(position).getCarName());
        icon.setImageResource(R.drawable.car_icon);
        plate.setText(list.get(position).getReg_number());
        fuel.setText(list.get(position).getFuel_distance() + " km");
        //TODO izracunati razdaljinu nas i auta i to postaviti u distance
        distance.setText(String.valueOf(list.get(position).getDistanceFromMe()));

        return vi;
    }
}
