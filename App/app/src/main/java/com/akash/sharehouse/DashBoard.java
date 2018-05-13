package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.sharehouse.adapters.HouseAdapter;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.House;
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

public class DashBoard extends ListActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private HouseAdapter adapter;
    private DialogFragment saveDialog;
    private ArrayList<House> houses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar =  findViewById(R.id.toolbar);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initaddButton();

    }

    public void initaddButton(){
        FloatingActionButton addHouse = findViewById(R.id.addHouse);

        addHouse.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this,AddHouse.class);
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

        saveDialog = DashBoard.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Loading houses...");

        RequestBody formBody = new FormBody.Builder()
                .add("type", "request")
                .build();

        URLhelper urlResourceHelper =
                new URLhelper("fetchallmyhouse", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {


                        try {

                            JSONArray houseArray = obj.getJSONArray("House");
                            String user_name = obj.getString("username");
                            String user_email = obj.getString("useremail");

                            Type listType = new TypeToken<ArrayList<House>>(){}.getType();
                            houses = new Gson().fromJson(houseArray.toString(), listType);
                            Log.i("Test",houseArray.toString());

                            Iterator<House> itr = houses.iterator();
                            while(itr.hasNext()){
                                Log.i("Test",itr.next().toString());
                            }

                            if(houses.isEmpty()){
                                TextView info = findViewById(R.id.info);
                                info.setText("You are not part of any house.");
                            }
                            initAdapter();


                            TextView nav_username = findViewById(R.id.nav_username);
                            nav_username.setText("Username:"+user_name);
                            TextView nav_email = findViewById(R.id.nav_email);
                            nav_email.setText("Email:"+user_email);

                            saveDialog.dismiss();

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
            adapter = new HouseAdapter(DashBoard.this,houses);
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
                    Intent intent = new Intent(DashBoard.this, ViewHouse.class);
                    intent.putExtra("House", selectedHouse);
                    startActivity(intent);

                }
            });
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_change_password) {

            Intent intent = new Intent(DashBoard.this, ChangePassword.class);
            startActivity(intent);

        }else if (id == R.id.nav_payment) {
            Intent intent = new Intent(DashBoard.this, PaymentActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_edit_house) {
            Intent intent = new Intent(DashBoard.this, AdminHouseList.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(DashBoard.this, EditProfile.class);
            startActivity(intent);
        }else if(id == R.id.nav_about){
            Intent intent = new Intent(DashBoard.this, About.class);
            startActivity(intent);
        }else if (id == R.id.nav_logout) {
            // TODO :ask service to destroy session

            RequestBody formBody = new FormBody.Builder()
                    .add("type", "request")
                    .build();

            URLhelper urlResourceHelper =
                    new URLhelper("logout", formBody, new URLhelper.onFinishListener() {
                        @Override
                        public void onFinishSuccess(JSONObject obj) {


                            try {
                                String state = obj.getString("type");

                                    if(state.equalsIgnoreCase("success")){
                                        Intent intent = new Intent(DashBoard.this, Login.class);
                                        startActivity(intent);
                                    }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error reading server response", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onFinishFailed(String msg) {
                            saveDialog.dismiss();
                            Intent intent = new Intent(DashBoard.this, Login.class);
                            startActivity(intent);
                        }
                    });

            urlResourceHelper.execute();
;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static DashBoard.ProgressDialogFragment newInstance() {
            return new DashBoard.ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading Houses...");
            dialog.setIndeterminate(true);

            return dialog;
        }

    }
}
