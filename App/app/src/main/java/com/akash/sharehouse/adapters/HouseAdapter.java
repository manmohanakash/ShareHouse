package com.akash.sharehouse.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.akash.sharehouse.R;
import com.akash.sharehouse.SelectWeek;
import com.akash.sharehouse.ViewHouse;
import com.akash.sharehouse.models.House;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class HouseAdapter  extends ArrayAdapter<House> {

    private ArrayList<House> houses;
    private Context adapterContext;

    public HouseAdapter(Context context, ArrayList<House> houses) {
        super(context, R.layout.house_item, houses);
        adapterContext = context;
        this.houses = houses;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        try {
            final House house = houses.get(position);

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.house_item, null);
            }


            TextView housename = v.findViewById(R.id.houseitem_name);
            housename.setText(house.getHouseName().toUpperCase());
            ImageView  houseimg = v.findViewById(R.id.houseitem_image);
            Picasso.get().load(house.getHouseImg()).fit().memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(houseimg);

            Button view_button = v.findViewById(R.id.houseitem_details);

            view_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(adapterContext,ViewHouse.class);
                    intent.putExtra("House",house);
                    adapterContext.startActivity(intent);
                }
            });


            Button selectWeekButton = v.findViewById(R.id.houseitem_book);

            selectWeekButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(adapterContext,SelectWeek.class);
                    intent.putExtra("House",house);
                    adapterContext.startActivity(intent);
                }
            });


        }
        catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }
}
