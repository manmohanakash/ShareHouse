package com.example.akash.sharehouse;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.akash.sharehouse.models.House;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ViewHouse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_house);



        Intent lastintent = getIntent();
        House house = (House) lastintent.getSerializableExtra("House");

        TextView house_name = findViewById(R.id.houseView_name);
        TextView house_no = findViewById(R.id.houseView_no);
        TextView house_street = findViewById(R.id.houseView_street);
        TextView house_city = findViewById(R.id.houseView_city);
        TextView house_state = findViewById(R.id.houseView_state);
        TextView house_country = findViewById(R.id.houseView_country);
        TextView house_landmark = findViewById(R.id.houseView_landmark);
        TextView house_description = findViewById(R.id.houseView_description);
        TextView house_x = findViewById(R.id.houseView_x);
        TextView house_y = findViewById(R.id.houseView_y);

        house_name.setText(house.getHouseName());
        house_no.setText(house.getHouseNo());
        house_street.setText(house.getHouseStreet());
        house_city.setText(house.getHouseCity());
        house_state.setText(house.getHouseState());
        house_country.setText(house.getHouseCountry());
        house_landmark.setText(house.getLandmark());
        house_description.setText(house.getDescription());
        house_x.setText(house.getMapX());
        house_y.setText(house.getMapY());

        ImageView houseimg = findViewById(R.id.houseView_image);
        Picasso.get().load(house.getHouseImg()).fit().into(houseimg);


        initHomeButton();

    }

    public void initHomeButton() {
        ImageButton button_register = findViewById(R.id.home_icon);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ViewHouse.this, DashBoard.class);
                startActivity(intent);

            }
        });
    }


}
