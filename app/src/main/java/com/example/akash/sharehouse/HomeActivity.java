package com.example.akash.sharehouse;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.akash.sharehouse.helpers.URLhelper;
import com.example.akash.sharehouse.models.House;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class HomeActivity extends ListActivity {


    private HouseAdapter adapter;
    private DialogFragment saveDialog;
    private ArrayList<House> houses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initProfileButton();

    }


    public void initProfileButton(){
        ImageButton button_register = findViewById(R.id.dashboard_icon);
        button_register.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,EditProfile.class);
                startActivity(intent);


            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        initFetchData();

    }

    public void initFetchData(){

        saveDialog = EditProfile.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching User Details");

        RequestBody formBody = new FormBody.Builder()
                .add("type", "request")
                .build();

        URLhelper urlResourceHelper =
                new URLhelper("fetchallmyhouse", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {

                        saveDialog.dismiss();

                        try {
                            JSONArray houseArray = obj.getJSONArray("House");
                            Type listType = new TypeToken<ArrayList<House>>(){}.getType();
                            houses = new Gson().fromJson(houseArray.toString(), listType);
                            Log.i("Test",houseArray.toString());

                            Iterator<House> itr = houses.iterator();
                            while(itr.hasNext()){
                                Log.i("Test",itr.next().toString());
                            }
                            initAdapter();
                        } catch (JSONException e) {
                            Log.i("Test", e.toString());
                            Toast.makeText(getApplicationContext(), "Error Fetching User Profile", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        saveDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

        urlResourceHelper.execute();

    }

    public void initAdapter() {

        try{
        adapter = new HouseAdapter(HomeActivity.this,houses);
        setListAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving Houses", Toast.LENGTH_LONG).show();
        }


        if (houses.size() > 0) {
            ListView listView = getListView();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                    House selectedHouse = houses.get(position);
                    Intent intent = new Intent(HomeActivity.this, ViewHouse.class);
                    intent.putExtra("House", selectedHouse);
                    startActivity(intent);

                }
            });
        }

    }


}