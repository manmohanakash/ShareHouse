package com.akash.sharehouse.adapters;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.sharehouse.EditHouse;
import com.akash.sharehouse.R;
import com.akash.sharehouse.SelectWeek;
import com.akash.sharehouse.helpers.URLhelper;
import com.akash.sharehouse.models.House;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class WeekAdapter extends ArrayAdapter<Integer> {

    private ArrayList<Integer> weeks;
    private DialogFragment saveDialog;
    private Context adapterContext;
    private House house;
    private LinkedHashMap<Integer,String> mapforadapter;
    private ImageButton deleteweek_icon;

    public WeekAdapter(Context context, ArrayList<Integer> weeks, House house, DialogFragment saveDialog,LinkedHashMap<Integer,String> mapforadapter) {
        super(context, R.layout.week_item, weeks);
        adapterContext = context;
        this.weeks = weeks;
        this.house = house;
        this.saveDialog = saveDialog;
        this.mapforadapter = mapforadapter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        try {
            final int weekid = weeks.get(position);

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.week_item, null);
            }


            TextView user_name = v.findViewById(R.id.week_name);
            user_name.setText(mapforadapter.get(weekid));

            deleteweek_icon = v.findViewById(R.id.deleteweek_icon);

            Calendar call = Calendar.getInstance();
            int callweek = call.get(Calendar.WEEK_OF_YEAR);

            if(weekid<callweek+2) {
                deleteweek_icon.setBackgroundResource(R.mipmap.lock);
                deleteweek_icon.setEnabled(false);
            }else{
                deleteweek_icon.setVisibility(View.VISIBLE);
            }

            deleteweek_icon.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(adapterContext);
                    builder.setMessage("CONFIRM DELETE BOOKING:\n"+mapforadapter.get(weekid));

                    builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            saveDialog = SelectWeek.ProgressDialogFragment.newInstance();
                            saveDialog.show(((Activity) adapterContext).getFragmentManager(), "Deleting a booking");

                            JSONObject request = new JSONObject();
                            try {
                                request.put("HouseID",house.getHouseID());
                                request.put("WeekID",weekid);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody formBody = RequestBody.create(JSON, request.toString());
                            URLhelper urlResourceHelper =
                                    new URLhelper("removemybooking", formBody, new URLhelper.onFinishListener() {

                                        @Override
                                        public void onFinishSuccess(JSONObject obj) {
                                            String msg = null;
                                            try {
                                                msg = obj.getString("message");
                                                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getContext(),SelectWeek.class);
                                                intent.putExtra("House",house);
                                                getContext().startActivity(intent);
                                                saveDialog.dismiss();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFinishFailed(String msg) {
                                            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            urlResourceHelper.execute();
                        }
                    });



                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //perform any action
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

