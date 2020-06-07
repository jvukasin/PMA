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

public class FavoriteCarsAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<CarItem> list;

    public FavoriteCarsAdapter(Activity act, ArrayList<CarItem> list) {
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
            vi = activity.getLayoutInflater().inflate(R.layout.favorite_car_item, null);
        }

        TextView name = (TextView) vi.findViewById(R.id.favCarName);
        ImageView icon = (ImageView) vi.findViewById(R.id.favCarIcon);
        TextView plate = (TextView) vi.findViewById(R.id.favCarPlate);
        TextView mileage = (TextView) vi.findViewById(R.id.favCarMileage);

        name.setText(list.get(position).getCarName());
        icon.setImageResource(R.drawable.car_icon);
        plate.setText(list.get(position).getReg_number());
        mileage.setText(" " + list.get(position).getMileage() + " km");

        return vi;
    }
}