package com.akash.sharehouse;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.sharehouse.adapters.HouseAdminAdapter;
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

public class AdminHouseList extends ListActivity {

    private HouseAdminAdapter adapter;
    private DialogFragment saveDialog;
    private ArrayList<House> houses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_house_list);

        initaddButton();
        initHomeButton();
    }

    @Override
    public void onResume() {
        super.onResume();

        initFetchData();

    }



    public void initaddButton(){
        FloatingActionButton addHouse = findViewById(R.id.addHouse);

        addHouse.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(AdminHouseList.this,AddHouse.class);
                startActivity(intent);
            }
        });

    }

    public void initHomeButton() {
        ImageButton button_register = findViewById(R.id.home_icon);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AdminHouseList.this, DashBoard.class);
                startActivity(intent);

            }
        });
    }


    public void initFetchData(){

        saveDialog = AdminHouseList.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching User Details");

        RequestBody formBody = new FormBody.Builder()
                .add("type", "request")
                .build();

        URLhelper urlResourceHelper =
                new URLhelper("fetchalladminhouse", formBody, new URLhelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {

                        try {

                            JSONArray houseArray = obj.getJSONArray("House");

                            Type listType = new TypeToken<ArrayList<House>>(){}.getType();
                            houses = new Gson().fromJson(houseArray.toString(), listType);

                            if(houses.isEmpty()){
                                TextView info = findViewById(R.id.info);
                                info.setText("You are not an admin to any house.\nCreate a new house to be the admin.");
                            }
                            initAdapter();
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
            adapter = new HouseAdminAdapter(AdminHouseList.this,houses,saveDialog);
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
                    Intent intent = new Intent(AdminHouseList.this, ViewHouse.class);
                    intent.putExtra("House", selectedHouse);
                    startActivity(intent);

                }
            });
        }

    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static AdminHouseList.ProgressDialogFragment newInstance() {
            return new AdminHouseList.ProgressDialogFragment();
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
