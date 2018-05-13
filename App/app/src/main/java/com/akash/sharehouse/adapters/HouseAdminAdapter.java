package com.akash.sharehouse.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.sharehouse.AdminHouseList;
import com.akash.sharehouse.EditHouse;
import com.akash.sharehouse.R;
import com.akash.sharehouse.SelectWeek;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.House;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class HouseAdminAdapter  extends ArrayAdapter<House> {

    private ArrayList<House> houses;
    private Context adapterContext;
    private DialogFragment saveDialog;

    public HouseAdminAdapter(Context context, ArrayList<House> houses,DialogFragment saveDialog) {
        super(context, R.layout.admin_house_item, houses);
        adapterContext = context;
        this.houses = houses;
        this.saveDialog = saveDialog;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        try {
            final House house = houses.get(position);

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.admin_house_item, null);
            }


            TextView housename = v.findViewById(R.id.admin_houseitem_name);
            housename.setText(house.getHouseName().toUpperCase());
            ImageView  houseimg = v.findViewById(R.id.admin_houseitem_image);
            Picasso.get().load(house.getHouseImg()).fit().into(houseimg);

            Button admin_houseitem_edit = v.findViewById(R.id.admin_houseitem_edit);

            admin_houseitem_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(adapterContext,EditHouse.class);
                    intent.putExtra("House",house);
                    adapterContext.startActivity(intent);
                }
            });

            Button delete_button = v.findViewById(R.id.admin_houseitem_delete);

            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(adapterContext);
                    builder.setMessage("CONFIRM DELETE");

                    builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            saveDialog = AdminHouseList.ProgressDialogFragment.newInstance();
                            saveDialog.show(((Activity) adapterContext).getFragmentManager(), "Deleting House ");

                            JSONObject request = new JSONObject();

                            try {
                                request.put("HouseID",house.getHouseID());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody formBody = RequestBody.create(JSON, request.toString());


                            URLhelper urlResourceHelper =
                                    new URLhelper("deletehouse", formBody, new URLhelper.onFinishListener() {
                                        @Override
                                        public void onFinishSuccess(JSONObject obj){
                                            try {
                                                String msg = obj.getString("message");
                                                Toast.makeText(adapterContext.getApplicationContext(),msg, Toast.LENGTH_SHORT).show();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Intent intent = new Intent(adapterContext.getApplicationContext(),AdminHouseList.class);
                                            adapterContext.startActivity(intent);
                                            saveDialog.dismiss();
                                        }

                                        @Override
                                        public void onFinishFailed(String msg) {
                                            Toast.makeText(adapterContext.getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
                                            saveDialog.dismiss();
                                        }
                                    });
                            urlResourceHelper.execute();
                        }
                    });


                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //perform any action
                            //Toast.makeText(adapterContext.getApplicationContext(), "CANCELED", Toast.LENGTH_SHORT).show();
                        }
                    });

                    //creating alert dialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


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
