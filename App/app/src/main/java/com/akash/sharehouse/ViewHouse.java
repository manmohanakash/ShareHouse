package com.akash.sharehouse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akash.sharehouse.models.House;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class ViewHouse extends AppCompatActivity implements OnMapReadyCallback{


    GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    private House house;
    private SupportMapFragment mapFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_house);

        Intent lastintent = getIntent();

        house = (House) lastintent.getSerializableExtra("House");

            TextView house_name = findViewById(R.id.houseView_name);
            TextView house_no = findViewById(R.id.houseView_no);
            TextView house_street = findViewById(R.id.houseView_street);
            TextView house_city = findViewById(R.id.houseView_city);
            TextView house_state = findViewById(R.id.houseView_state);
            TextView house_country = findViewById(R.id.houseView_country);
            TextView house_landmark = findViewById(R.id.houseView_landmark);
            TextView house_description = findViewById(R.id.houseView_description);
            TextView house_maxWeeks = findViewById(R.id.houseview_maxweeks);
            TextView house_weeklyRate = findViewById(R.id.houseview_weeklyrate);
            TextView house_sharedBy = findViewById(R.id.houseview_shareby);

            house_name.setText(house.getHouseName().toUpperCase());
            house_no.setText(house.getHouseNo().toUpperCase());
            house_street.setText(house.getHouseStreet().toUpperCase());
            house_city.setText(house.getHouseCity().toUpperCase());
            house_state.setText(house.getHouseState().toUpperCase());
            house_country.setText(house.getHouseCountry().toUpperCase());
            house_landmark.setText(house.getLandmark().toUpperCase());
            house_description.setText(house.getDescription().toUpperCase());
            house_maxWeeks.setText(house.getMaxWeeks().toString());
            house_weeklyRate.setText(house.getWeekRate().toString()+"$");
            house_sharedBy.setText(house.getSharedCounter().toString());

            ImageView houseimg = findViewById(R.id.houseView_image);
            Picasso.get().load(house.getHouseImg()).fit().into(houseimg);

            initHomeButton();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ViewHouse.this);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
        }

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

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (house.getMapX()==null || house.getMapY()==null) {

            mapFragment.isVisible();
        }
        else {
            gMap = googleMap;
            gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            Point size = new Point();
            WindowManager w = getWindowManager();
            w.getDefaultDisplay().getSize(size);
            int measuredWidth = size.x;
            int measuredHeight = size.y;

            Double lat = Double.parseDouble(house.getMapX());
            Double lng = Double.parseDouble(house.getMapY());
            LatLng point = new LatLng(lat, lng);
            builder.include(point);

            gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    Context context = getApplicationContext();


                    LinearLayout info = new LinearLayout(context);

                    info.setOrientation(LinearLayout.VERTICAL);


                    TextView title = new TextView(context);
                    title.setTextColor(Color.BLACK);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());
                    TextView snippet = new TextView(context);
                    snippet.setTextColor(Color.BLACK);
                    snippet.setText(marker.getSnippet());
                    info.addView(title);
                    info.addView(snippet);
                    return info;
                }
            });


            gMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title(house.getHouseName())
                    .snippet(house.getHouseCity())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)))
                    .showInfoWindow();


            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
        }

    }

}
